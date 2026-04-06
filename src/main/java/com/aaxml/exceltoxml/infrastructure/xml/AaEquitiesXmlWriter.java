package com.aaxml.exceltoxml.infrastructure.xml;

import com.aaxml.exceltoxml.domain.model.HoldingRecord;
import com.aaxml.exceltoxml.domain.model.TransactionRecord;
import com.aaxml.exceltoxml.domain.model.TransactionsMetadata;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AaEquitiesXmlWriter {

    private static final String NL = System.lineSeparator();
    private static final String HOLDING_PREFIX = "  <Holding ";
    private static final String TRANSACTION_PREFIX = "  <Transaction ";
    private static final String HOLDINGS_OPEN = "<Holdings type=\"DEMAT\">";
    private static final String HOLDINGS_CLOSE = "</Holdings>";
    private static final String TRANSACTIONS_OPEN = "<Transactions ";
    private static final String TRANSACTIONS_CLOSE = "</Transactions>";

    public String writeHoldings(List<HoldingRecord> records) {
        StringBuilder xml = new StringBuilder(estimateCapacity(records.size(), 120));
        xml.append(HOLDINGS_OPEN).append(NL);

        for (HoldingRecord record : records) {
            xml.append(HOLDING_PREFIX)
                    .append(attribute("issuerName", record.issuerName()))
                    .append(NL)
                    .append(attributeLine("isin", record.isin(), 4))
                    .append(attributeLine("isinDescription", record.isinDescription(), 4))
                    .append(attributeLine("units", record.units(), 4))
                    .append(lastAttributeSelfClosingLine("lastTradedPrice", record.lastTradedPrice(), 4));
        }

        xml.append(HOLDINGS_CLOSE);
        return xml.toString();
    }

    public String writeTransactions(TransactionsMetadata metadata, List<TransactionRecord> records) {
        StringBuilder xml = new StringBuilder(estimateCapacity(records.size(), 280));
        xml.append(TRANSACTIONS_OPEN)
                .append(attribute("startDate", metadata.startDate()))
                .append(NL)
                .append(lastAttributeOpenTagLine("endDate", metadata.endDate(), 2));

        for (TransactionRecord record : records) {
            xml.append(TRANSACTION_PREFIX)
                    .append(attribute("txnId", record.txnId()))
                    .append(NL)
                    .append(attributeLine("orderId", record.orderId(), 4))
                    .append(attributeLine("companyName", record.companyName(), 4))
                    .append(attributeLine("transactionDateTime", record.transactionDateTime(), 4))
                    .append(attributeLine("exchange", record.exchange(), 4))
                    .append(attributeLine("isin", record.isin(), 4))
                    .append(attributeLine("isinDescription", record.isinDescription(), 4))
                    .append(attributeLine("equityCategory", record.equityCategory(), 4))
                    .append(attributeLine("narration", record.narration(), 4))
                    .append(attributeLine("rate", record.rate(), 4))
                    .append(attributeLine("type", record.type(), 4))
                    .append(lastAttributeSelfClosingLine("units", record.units(), 4));
        }

        xml.append(TRANSACTIONS_CLOSE);
        return xml.toString();
    }

    private int estimateCapacity(int rowCount, int averageRowSize) {
        return Math.max(256, rowCount * averageRowSize);
    }

    private String attribute(String name, String value) {
        return name + "=\"" + escape(value) + "\"";
    }

    private String attributeLine(String name, String value, int indent) {
        return " ".repeat(indent) + attribute(name, value) + NL;
    }

    private String lastAttributeSelfClosingLine(String name, String value, int indent) {
        return " ".repeat(indent) + attribute(name, value) + "/>" + NL;
    }

    private String lastAttributeOpenTagLine(String name, String value, int indent) {
        return " ".repeat(indent) + attribute(name, value) + ">" + NL;
    }

    private String escape(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}