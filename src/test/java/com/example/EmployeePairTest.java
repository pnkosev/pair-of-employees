package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeePairTest {

    @Test
    void addCommonProjectWhenFirstEntry() {
        Employee employeeOne = new Employee(1, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeTwo = new Employee(2, LocalDate.parse("2022-01-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairOne = new EmployeePair(employeeOne, employeeTwo);

        employeePairOne.addCommonProject(1, 365);

        assertEquals(1, employeePairOne.getCommonProjects().size());
        assertEquals(365, employeePairOne.getTotalOverlappingDays());
    }

    @Test
    void addCommonProjectShouldSumTotalOverlappingDaysWhenSecondOverlapOccurs() {
        Employee employeeOne = new Employee(1, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeTwo = new Employee(2, LocalDate.parse("2022-01-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairOne = new EmployeePair(employeeOne, employeeTwo);

        employeePairOne.addCommonProject(1, 365);
        assertEquals(1, employeePairOne.getCommonProjects().size());

        employeePairOne.addCommonProject(1, 412);
        assertEquals(777, employeePairOne.getCommonProjects().get(1L));
        assertEquals(777, employeePairOne.getTotalOverlappingDays());
    }

    @Test
    void testEqualsPositive() {
        Employee employeeOne = new Employee(1, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeTwo = new Employee(2, LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairOne = new EmployeePair(employeeOne, employeeTwo);

        Employee otherEmployeeOne = new Employee(1, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee otherEmployeeTwo = new Employee(2, LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairTwo = new EmployeePair(otherEmployeeTwo, otherEmployeeOne);

        assertEquals(employeePairOne, employeePairTwo);
        assertEquals(employeePairTwo, employeePairOne);
    }

    @Test
    void testEqualsNegative() {
        Employee employeeOne = new Employee(1, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeTwo = new Employee(2, LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairOne = new EmployeePair(employeeOne, employeeTwo);

        Employee employeeThree = new Employee(3, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeFour = new Employee(4, LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairTwo = new EmployeePair(employeeThree, employeeFour);

        assertNotEquals(employeePairOne, employeePairTwo);
        assertNotEquals(employeePairTwo, employeePairOne);
    }

    @Test
    void hashCodeShouldReturnSumOfIds() {
        Employee employeeOne = new Employee(1, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeSeven = new Employee(7, LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairOne = new EmployeePair(employeeOne, employeeSeven);

        Employee employeeTwo = new Employee(2, LocalDate.parse("2021-01-01"), LocalDate.parse("2023-01-01"));
        Employee employeeSix = new Employee(6, LocalDate.parse("2022-06-01"), LocalDate.parse("2023-01-01"));
        EmployeePair employeePairTwo = new EmployeePair(employeeTwo, employeeSix);

        assertEquals(8, employeePairOne.hashCode());
        assertEquals(8, employeePairTwo.hashCode());
        assertEquals(employeePairOne.hashCode(), employeePairTwo.hashCode());
    }
}