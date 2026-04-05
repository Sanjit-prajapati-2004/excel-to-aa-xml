package com.aaxml.exceltoxml.application.service;

import com.aaxml.exceltoxml.domain.model.TransactionsMetadata;
import com.aaxml.exceltoxml.infrastructure.excel.ExcelWorkbookParser;
import com.aaxml.exceltoxml.infrastructure.xml.AaEquitiesXmlWriter;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ConversionApplicationService {

    private final ExcelWorkbookParser excelWorkbookParser;
    private final AaEquitiesXmlWriter aaEquitiesXmlWriter;

    public ConversionApplicationService(
            ExcelWorkbookParser excelWorkbookParser,
            AaEquitiesXmlWriter aaEquitiesXmlWriter
    ) {
        this.excelWorkbookParser = excelWorkbookParser;
        this.aaEquitiesXmlWriter = aaEquitiesXmlWriter;
    }

    public String convertHoldings(MultipartFile file) throws IOException {
        return aaEquitiesXmlWriter.writeHoldings(excelWorkbookParser.parseHoldings(file));
    }

    public String convertTransactions(MultipartFile file, TransactionsMetadata metadata) throws IOException {
        return aaEquitiesXmlWriter.writeTransactions(metadata, excelWorkbookParser.parseTransactions(file));
    }
}
