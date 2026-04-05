package com.aaxml.exceltoxml.domain.model;

public record TransactionRecord(
        String txnId,
        String orderId,
        String companyName,
        String transactionDateTime,
        String exchange,
        String isin,
        String isinDescription,
        String equityCategory,
        String narration,
        String rate,
        String type,
        String units
) {
}
