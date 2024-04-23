package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EmployeePair implements Comparable<EmployeePair> {
    private final Employee one;
    private final Employee two;
    private final Map<Long, Long> commonProjects = new HashMap<>();
    private long totalOverlappingDays = 0;

    public EmployeePair(Employee one, Employee two) {
        this.one = one;
        this.two = two;
    }

    public Map<Long, Long> getCommonProjects() {
        return commonProjects;
    }

    public long getTotalOverlappingDays() {
        return totalOverlappingDays;
    }

    public void addCommonProject(long projectId, long days) {
        long totalDays = days;
        if (commonProjects.containsKey(projectId)) {
            totalDays += commonProjects.get(projectId);
        }
        commonProjects.put(projectId, totalDays);
        totalOverlappingDays += days;
    }

    public void addCommonProject(Map<Long, Long> commonProjects) {
        for (Map.Entry<Long, Long> entry : commonProjects.entrySet()) {
            addCommonProject(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Long, Long> commonProject : commonProjects.entrySet()) {
            result.append(String.format("%d, %d, %d, %d",
                    one.getId(), two.getId(), commonProject.getKey(), commonProject.getValue()));
            result.append(System.lineSeparator());
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmployeePair that = (EmployeePair) o;
        return Objects.equals(one.getId(), that.one.getId()) && Objects.equals(two.getId(), that.two.getId())
            || Objects.equals(two.getId(), that.one.getId()) && Objects.equals(one.getId(), that.two.getId());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(one.getId() + two.getId());
    }

    @Override
    public int compareTo(EmployeePair o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return Long.compare(totalOverlappingDays, o.totalOverlappingDays);
    }
}
