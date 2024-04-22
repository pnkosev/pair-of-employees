package com.example;

public class EmployeePair {
    private final Employee one;
    private final Employee two;
    private final long totalOverlappedDays;
    private final long projectId;

    public EmployeePair(Employee one, Employee two, long totalOverlappedDays, long projectId) {
        this.one = one;
        this.two = two;
        this.totalOverlappedDays = totalOverlappedDays;
        this.projectId = projectId;
    }

    public long getTotalOverlappedDays() {
        return totalOverlappedDays;
    }

    @Override
    public String toString() {
        return String.format("%d, %d, %d", one.getId(), two.getId(), projectId);
    }
}
