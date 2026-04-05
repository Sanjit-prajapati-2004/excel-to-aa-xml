package com.aaxml.exceltoxml.infrastructure.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelTemplateFactory {

    private static final Set<String> INTEGER_COLUMNS = Set.of("units");
    private static final Set<String> DECIMAL_COLUMNS = Set.of("lastTradedPrice", "rate");

    public byte[] createHoldingsTemplate() throws IOException {
        return createWorkbook(
                "Holdings",
                List.of("issuerName", "isin", "isinDescription", "units", "lastTradedPrice")
        );
    }

    public byte[] createTransactionsTemplate() throws IOException {
        return createWorkbook(
                "Transactions",
                List.of(
                        "txnId", "orderId", "companyName", "transactionDateTime", "exchange", "isin",
                        "isinDescription", "equityCategory", "narration", "rate", "type", "units"
                )
        );
    }

    private byte[] createWorkbook(String sheetName, List<String> headers) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle integerStyle = createNumericStyle(workbook, "0");
            CellStyle decimalStyle = createNumericStyle(workbook, "0.############");

            Row headerRow = sheet.createRow(0);

            for (int index = 0; index < headers.size(); index++) {
                String header = headers.get(index);
                Cell headerCell = headerRow.createCell(index);
                headerCell.setCellValue(header);
                headerCell.setCellStyle(headerStyle);
                sheet.setColumnWidth(index, 4500);

                if (INTEGER_COLUMNS.contains(header)) {
                    sheet.setDefaultColumnStyle(index, integerStyle);
                } else if (DECIMAL_COLUMNS.contains(header)) {
                    sheet.setDefaultColumnStyle(index, decimalStyle);
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createNumericStyle(XSSFWorkbook workbook, String formatPattern) {
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(dataFormat.getFormat(formatPattern));
        return style;
    }
}