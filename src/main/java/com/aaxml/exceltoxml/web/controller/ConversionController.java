package com.aaxml.exceltoxml.web.controller;

import com.aaxml.exceltoxml.application.service.ConversionApplicationService;
import com.aaxml.exceltoxml.application.service.TemplateApplicationService;
import com.aaxml.exceltoxml.domain.model.TransactionsMetadata;
import java.io.IOException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ConversionController {

    private final TemplateApplicationService templateApplicationService;
    private final ConversionApplicationService conversionApplicationService;

    public ConversionController(
            TemplateApplicationService templateApplicationService,
            ConversionApplicationService conversionApplicationService
    ) {
        this.templateApplicationService = templateApplicationService;
        this.conversionApplicationService = conversionApplicationService;
    }

    @GetMapping("/templates/holdings")
    public ResponseEntity<byte[]> downloadHoldingsTemplate() throws IOException {
        return buildTemplateResponse(templateApplicationService.generateHoldingsTemplate(), "holdings-template.xlsx");
    }

    @GetMapping("/templates/transactions")
    public ResponseEntity<byte[]> downloadTransactionsTemplate() throws IOException {
        return buildTemplateResponse(templateApplicationService.generateTransactionsTemplate(), "transactions-template.xlsx");
    }

    @PostMapping(value = "/convert/holdings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertHoldings(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(conversionApplicationService.convertHoldings(file));
    }

    @PostMapping(value = "/convert/transactions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertTransactions(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(
                conversionApplicationService.convertTransactions(file, TransactionsMetadata.forCurrentMonthWindow())
        );
    }

    private ResponseEntity<byte[]> buildTemplateResponse(byte[] content, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        return ResponseEntity.ok().headers(headers).body(content);
    }
}