package com.aaxml.exceltoxml.application.service;

import com.aaxml.exceltoxml.infrastructure.excel.ExcelTemplateFactory;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class TemplateApplicationService {

    private final ExcelTemplateFactory excelTemplateFactory;

    public TemplateApplicationService(ExcelTemplateFactory excelTemplateFactory) {
        this.excelTemplateFactory = excelTemplateFactory;
    }

    public byte[] generateHoldingsTemplate() throws IOException {
        return excelTemplateFactory.createHoldingsTemplate();
    }

    public byte[] generateTransactionsTemplate() throws IOException {
        return excelTemplateFactory.createTransactionsTemplate();
    }
}
