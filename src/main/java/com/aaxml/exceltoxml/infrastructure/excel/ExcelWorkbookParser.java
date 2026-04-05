package com.aaxml.exceltoxml.infrastructure.excel;

import com.aaxml.exceltoxml.common.exception.InvalidExcelTemplateException;
import com.aaxml.exceltoxml.domain.model.HoldingRecord;
import com.aaxml.exceltoxml.domain.model.TransactionRecord;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExcelWorkbookParser {

    private static final List<String> HOLDINGS_HEADERS = List.of(
            "issuerName", "isin", "isinDescription", "units", "lastTradedPrice"
    );

    private static final List<String> TRANSACTIONS_HEADERS = List.of(
            "txnId", "orderId", "companyName", "transactionDateTime", "exchange", "isin",
            "isinDescription", "equityCategory", "narration", "rate", "type", "units"
    );

    private final DataFormatter dataFormatter = new DataFormatter();

    public List<HoldingRecord> parseHoldings(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            validateHeaders(sheet, HOLDINGS_HEADERS);
            List<HoldingRecord> records = new ArrayList<>();

            Iterator<Row> iterator = sheet.rowIterator();
            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (isBlankRow(row, HOLDINGS_HEADERS.size())) {
                    continue;
                }

                records.add(new HoldingRecord(
                        readCell(row, 0),
                        readCell(row, 1),
                        readCell(row, 2),
                        readCell(row, 3),
                        readCell(row, 4)
                ));
            }

            if (records.isEmpty()) {
                throw new InvalidExcelTemplateException("The uploaded holdings sheet does not contain any data rows.");
            }

            return records;
        }
    }

    public List<TransactionRecord> parseTransactions(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            validateHeaders(sheet, TRANSACTIONS_HEADERS);
            List<TransactionRecord> records = new ArrayList<>();

            Iterator<Row> iterator = sheet.rowIterator();
            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (isBlankRow(row, TRANSACTIONS_HEADERS.size())) {
                    continue;
                }

                records.add(new TransactionRecord(
                        readCell(row, 0),
                        readCell(row, 1),
                        readCell(row, 2),
                        readCell(row, 3),
                        readCell(row, 4),
                        readCell(row, 5),
                        readCell(row, 6),
                        readCell(row, 7),
                        readCell(row, 8),
                        readCell(row, 9),
                        readCell(row, 10),
                        readCell(row, 11)
                ));
            }

            if (records.isEmpty()) {
                throw new InvalidExcelTemplateException("The uploaded transactions sheet does not contain any data rows.");
            }

            return records;
        }
    }

    private void validateHeaders(Sheet sheet, List<String> expectedHeaders) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null || headerRow.getLastCellNum() != expectedHeaders.size()) {
            throw new InvalidExcelTemplateException("Invalid Excel file");
        }

        for (int index = 0; index < expectedHeaders.size(); index++) {
            String actualHeader = readCell(headerRow, index);
            String expectedHeader = expectedHeaders.get(index);
            if (!expectedHeader.equals(actualHeader)) {
                throw new InvalidExcelTemplateException("Invalid Excel file");
            }
        }
    }

    private boolean isBlankRow(Row row, int columnCount) {
        for (int index = 0; index < columnCount; index++) {
            if (!readCell(row, index).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String readCell(Row row, int columnIndex) {
        return dataFormatter.formatCellValue(row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
    }
}