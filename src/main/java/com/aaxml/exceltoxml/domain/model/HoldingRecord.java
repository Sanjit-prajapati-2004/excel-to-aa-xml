package com.aaxml.exceltoxml.domain.model;

public record HoldingRecord(
        String issuerName,
        String isin,
        String isinDescription,
        String units,
        String lastTradedPrice
) {
}
