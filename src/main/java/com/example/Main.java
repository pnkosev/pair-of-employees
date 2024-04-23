package com.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static final String NULL_DATE_TO = "NULL";

    public static void main(String[] args) throws FileNotFoundException {
        Map<Long, List<Employee>> records;
        records = collectRecords("/test1.csv");

        TreeMap<EmployeePair, EmployeePair> employeePairs = new TreeMap<>(getEmployeePairs(records));

        if (employeePairs.isEmpty()) {
            System.out.println("No overlapping pair of employees found!");
        } else {
            System.out.println(employeePairs.lastEntry().getValue());
        }
    }

    /**
     * Aggregates the employee pairs who have ever worked on a common
     * project with the respective number of overlapping days per project
     * as well as their sum.
     *
     * @param records map of all projects with their corresponding
     *                list of employees ({@link Employee})
     * @return map of employee pairs ({@link EmployeePair}) as key and value
     */
    public static Map<EmployeePair, EmployeePair> getEmployeePairs(Map<Long, List<Employee>> records) {
        Map<EmployeePair, EmployeePair> employeePairs = new HashMap<>();

        for (Map.Entry<Long, List<Employee>> record : records.entrySet()) {
            List<Employee> employees = record.getValue();
            if (employees.size() > 1) {
                for (int i = 0; i < employees.size() - 1; i++) {
                    Employee employee = employees.get(i);
                    for (int j = i + 1; j < employees.size(); j++) {
                        Employee nextEmployee = employees.get(j);
                        if (employee.getId() != nextEmployee.getId()) {
                            long overlappingDays = calculateOverlappingDays(employee.getStartDate(), employee.getEndDate(),
                                    nextEmployee.getStartDate(), nextEmployee.getEndDate());
                            if (overlappingDays > 0) {
                                EmployeePair employeePair = new EmployeePair(employee, nextEmployee);
                                employeePair.addCommonProject(record.getKey(), overlappingDays);
                                if (employeePairs.containsKey(employeePair)) {
                                    Map<Long, Long> oldCommonProjects = employeePairs.get(employeePair).getCommonProjects();
                                    employeePair.addCommonProject(oldCommonProjects);
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

    /**
     * Aggregates project to employees data from a csv file with format:
     * EmployeeID, ProjectID, DateFrom, DateTo
     *
     * @param path the absolute path to the file
     * @return map with project IDs with corresponding list of employees
     *         ({@link Employee})
     */
    public static Map<Long, List<Employee>> collectRecords(String path) throws FileNotFoundException {
        Map<Long, List<Employee>> records = new HashMap<>();
        InputStream inputStream = Main.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new FileNotFoundException();
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try (BufferedReader br = new BufferedReader(streamReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",\\s*");
                long employeeId = Long.parseLong(values[0]);
                long projectId = Long.parseLong(values[1]);
                LocalDate from = parseDate(values[2]);
                String dateToAsString = values[3];
                LocalDate to;
                if (dateToAsString.equals(NULL_DATE_TO)) {
                    to = LocalDate.now();
                } else {
                    to = parseDate(values[3]);
                }

                records.computeIfAbsent(projectId, x -> new ArrayList<>())
                        .add(new Employee(employeeId, from, to));
            }
        } catch (IOException e) {
            // TODO error handling
        } catch (DateTimeParseException e) {
            // TODO error handling
        }
        return records;
    }

    /**
     * Calculates the number of overlapping days between two given time ranges.
     *
     * @param startDateOne start date of first time range
     * @param endDateOne end date of first time range
     * @param startDateTwo start date of second time range
     * @param endDateTwo end date of second time range
     * @return number of overlapping days
     */
    public static long calculateOverlappingDays(LocalDate startDateOne, LocalDate endDateOne,
                                                LocalDate startDateTwo, LocalDate endDateTwo) {
        long minEndDate = Math.min(endDateOne.toEpochDay(), endDateTwo.toEpochDay());
        long maxStartDate = Math.max(startDateOne.toEpochDay(), startDateTwo.toEpochDay());
        long overlap = minEndDate - maxStartDate;
        return Math.max(0, overlap);
    }

    /**
     * Parses a given date with the following formats:
     * <ul>
     *     <li>yyyy-MM-dd</li>
     *     <li>dd-MM-yyyy</li>
     * </ul>
     *
     * @param date String representation of the date
     * @return a parsed date
     */
    public static LocalDate parseDate(String date) {
        // Add different formats if required.
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("[yyyy-MM-dd]" + "[dd-MM-yyyy]"));
        DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
        return LocalDate.parse(date, dateTimeFormatter);
    }
}