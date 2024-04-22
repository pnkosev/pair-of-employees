package com.example;

import java.time.LocalDate;

public class Employee {
    private final long id;
    private final LocalDate startDate;
    private final LocalDate endDate;

    Employee(long id, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
