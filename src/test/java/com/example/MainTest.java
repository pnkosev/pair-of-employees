package com.example;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    void getEmployeePairsPositive() throws FileNotFoundException {
        Map<Long, List<Employee>> records = Main.collectRecords("/unit-test.csv");

        TreeMap<EmployeePair, EmployeePair> employeePairs = new TreeMap<>(Main.getEmployeePairs(records));

        assertFalse(employeePairs.isEmpty());
        assertEquals(2, employeePairs.keySet().size());
        EmployeePair firstPair = employeePairs.firstEntry().getValue();
        assertEquals(1, firstPair.getCommonProjects().size());
        assertEquals(1208, firstPair.getTotalOverlappingDays());
        EmployeePair lastPair = employeePairs.lastEntry().getValue();
        assertEquals(3, lastPair.getCommonProjects().size());
        assertEquals(1574, lastPair.getTotalOverlappingDays());
    }

    @Test
    void collectRecordsPositive() throws FileNotFoundException {
        Map<Long, List<Employee>> records = Main.collectRecords("/unit-test.csv");

        assertFalse(records.isEmpty());
        assertEquals(3, records.keySet().size());
        assertEquals(4, records.get(1L).size());
        assertEquals(2, records.get(2L).size());
        assertEquals(2, records.get(3L).size());
    }

    @Test
    void collectRecordsNegative() {
        assertThrows(FileNotFoundException.class, () -> Main.collectRecords("/test2.csv"));
    }

    @Test
    void parseDateYearMonthDay() {
        String dateAsString = "2024-01-01";

        assertDoesNotThrow(() -> Main.parseDate(dateAsString));
        assertEquals(LocalDate.parse(dateAsString), Main.parseDate(dateAsString));
    }

    @Test
    void parseDateDayMonthYear() {
        String dateAsString = "01-01-2024";

        assertDoesNotThrow(() -> Main.parseDate(dateAsString));
    }

    @Test
    void parseDateThrowsUponUnsupportedDateFormat() {
        String dateAsString = "01-30-2024";

        assertThrows(DateTimeParseException.class, () -> Main.parseDate(dateAsString));
    }

    @Test
    void calculateOverlappingDaysPositive31() {
        long overlappingDays = Main.calculateOverlappingDays(LocalDate.parse("2021-01-01"), LocalDate.parse("2021-02-01"),
                LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));

        assertEquals(31, overlappingDays);
    }

    @Test
    void calculateOverlappingDaysPositive365() {
        long overlappingDays = Main.calculateOverlappingDays(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"),
                LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));

        assertEquals(365, overlappingDays);
    }

    @Test
    void calculateOverlappingDaysNegativeBoundary() {
        long overlappingDays = Main.calculateOverlappingDays(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"),
                LocalDate.parse("2022-01-01"), LocalDate.parse("2023-01-01"));

        assertEquals(0, overlappingDays);
    }

    @Test
    void calculateOverlappingDaysNegativeExtreme() {
        long overlappingDays = Main.calculateOverlappingDays(LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"),
                LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));

        assertEquals(0, overlappingDays);
    }
}