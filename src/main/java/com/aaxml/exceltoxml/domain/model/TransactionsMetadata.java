package com.aaxml.exceltoxml.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public record TransactionsMetadata(
        String startDate,
        String endDate
) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static TransactionsMetadata forCurrentMonthWindow() {
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LocalDate startDate = endDate.minusYears(1);
        return new TransactionsMetadata(FORMATTER.format(startDate), FORMATTER.format(endDate));
    }
}