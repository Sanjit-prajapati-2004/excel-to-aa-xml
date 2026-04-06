package com.aaxml.exceltoxml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExcelToXmlApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void convertsHoldingsExcelWithMoreThanFiftyRows() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "holdings.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createHoldingsWorkbook(75)
        );

        mockMvc.perform(multipart("/api/convert/holdings").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    void convertsTransactionsExcelWithMoreThanFiftyRows() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "transactions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createTransactionsWorkbook(75)
        );

        mockMvc.perform(multipart("/api/convert/transactions").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    private byte[] createHoldingsWorkbook(int rows) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Holdings");
            sheet.createRow(0).createCell(0).setCellValue("issuerName");
            sheet.getRow(0).createCell(1).setCellValue("isin");
            sheet.getRow(0).createCell(2).setCellValue("isinDescription");
            sheet.getRow(0).createCell(3).setCellValue("units");
            sheet.getRow(0).createCell(4).setCellValue("lastTradedPrice");

            for (int i = 1; i <= rows; i++) {
                var row = sheet.createRow(i);
                row.createCell(0).setCellValue("Issuer " + i);
                row.createCell(1).setCellValue("INE043B01028");
                row.createCell(2).setCellValue("Issuer Description " + i);
                row.createCell(3).setCellValue("10");
                row.createCell(4).setCellValue("5.85");
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createTransactionsWorkbook(int rows) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Transactions");
            String[] headers = {
                    "txnId", "orderId", "companyName", "transactionDateTime", "exchange", "isin",
                    "isinDescription", "equityCategory", "narration", "rate", "type", "units"
            };

            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (int i = 1; i <= rows; i++) {
                var row = sheet.createRow(i);
                row.createCell(0).setCellValue("txn-" + i);
                row.createCell(1).setCellValue("100" + i);
                row.createCell(2).setCellValue("Company " + i);
                row.createCell(3).setCellValue("2025-10-01T00:00:00Z");
                row.createCell(4).setCellValue("NSE");
                row.createCell(5).setCellValue("INE043B01028");
                row.createCell(6).setCellValue("Description " + i);
                row.createCell(7).setCellValue("EQUITY");
                row.createCell(8).setCellValue("Narration " + i);
                row.createCell(9).setCellValue("5.85");
                row.createCell(10).setCellValue("BUY");
                row.createCell(11).setCellValue("10");
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}