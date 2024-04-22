package com.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<Long, List<Employee>> records;
        try {
            records = collectRecords("/test.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        EmployeePair pair = null;
        for (Map.Entry<Long, List<Employee>> record : records.entrySet()) {
            List<Employee> employees = record.getValue();
            if (employees.size() > 1) {
                for (int i = 0; i < employees.size() - 1; i++) {
                    Employee employee = employees.get(i);
                    for (int j = i + 1; j < employees.size(); j++) {
                        Employee otherEmployee = employees.get(j);
                        long overlappedDays = calculateOverlappedDays(employee, otherEmployee);
                        if (overlappedDays > 0) {
                            if (pair != null) {
                                if (overlappedDays > pair.getTotalOverlappedDays()) {
                                    pair = new EmployeePair(employee, otherEmployee, overlappedDays, record.getKey());
                                }
                            } else {
                                pair = new EmployeePair(employee, otherEmployee, overlappedDays, record.getKey());
                            }
                        }
                    }
                }
            }
        }

        if (pair != null) {
            System.out.println(pair);
        } else {
            System.out.println("No overlapping pair of employees found!");
        }
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

    private static long calculateOverlappedDays(Employee one, Employee two) {
        long minEndDate = Math.min(one.getEndDate().toEpochDay(), two.getEndDate().toEpochDay());
        long maxStartDate = Math.max(one.getStartDate().toEpochDay(), two.getStartDate().toEpochDay());
        long overlap = minEndDate - maxStartDate;
        return Math.max(0, overlap);
    }
}