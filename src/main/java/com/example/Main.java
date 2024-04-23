package com.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        Map<Long, List<Employee>> records;
        try {
            records = collectRecords("/test2.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        TreeMap<EmployeePair, EmployeePair> employeePairs = new TreeMap<>(getEmployeePairs(records));

        if (employeePairs.isEmpty()) {
            System.out.println("No overlapping pair of employees found!");
        } else {
            System.out.println(employeePairs.lastEntry().getValue());
        }
    }

    public static Map<EmployeePair, EmployeePair> getEmployeePairs(Map<Long, List<Employee>> records) {
        Map<EmployeePair, EmployeePair> employeePairs = new HashMap<>();

        for (Map.Entry<Long, List<Employee>> record : records.entrySet()) {
            List<Employee> employees = record.getValue();
            if (employees.size() > 1) {
                for (int i = 0; i < employees.size() - 1; i++) {
                    Employee employee = employees.get(i);
                    for (int j = i + 1; j < employees.size(); j++) {
                        Employee otherEmployee = employees.get(j);
                        if (employee.getId() != otherEmployee.getId()) {
                            long overlappingDays = calculateOverlappingDays(employee, otherEmployee);
                            if (overlappingDays > 0) {
                                EmployeePair employeePair = new EmployeePair(employee, otherEmployee);
                                employeePair.addCommonProject(record.getKey(), overlappingDays);
                                if (employeePairs.containsKey(employeePair)) {
                                    Map<Long, Long> previouslyKnownCommonProjects = employeePairs.get(employeePair).getCommonProjects();
                                    employeePair.addCommonProject(previouslyKnownCommonProjects);
                                    employeePairs.remove(employeePair);
                                }
                                employeePairs.put(employeePair, employeePair);
                            }
                        }
                    }
                }
            }
        }

        return employeePairs;
    }

    private static Map<Long, List<Employee>> collectRecords(String path) throws FileNotFoundException {
        Map<Long, List<Employee>> records = new HashMap<>();
        InputStream inputStream = Main.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new FileNotFoundException();
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try (BufferedReader br = new BufferedReader(streamReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(", ");
                long employeeId = Long.parseLong(values[0]);
                long projectId = Long.parseLong(values[1]);
                LocalDate from = LocalDate.parse(values[2]);
                LocalDate to;
                try {
                    to = LocalDate.parse(values[3]);
                } catch (DateTimeParseException e) {
                    to = LocalDate.now();
                }

                records.computeIfAbsent(projectId, x -> new ArrayList<>())
                        .add(new Employee(employeeId, from, to));
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    private static long calculateOverlappingDays(Employee one, Employee two) {
        long minEndDate = Math.min(one.getEndDate().toEpochDay(), two.getEndDate().toEpochDay());
        long maxStartDate = Math.max(one.getStartDate().toEpochDay(), two.getStartDate().toEpochDay());
        long overlap = minEndDate - maxStartDate;
        return Math.max(0, overlap);
    }
}