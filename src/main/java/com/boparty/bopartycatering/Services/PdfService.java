package com.boparty.bopartycatering.Services;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class PdfService {
    public void saveHtmlAsPdf(String htmlContent, String outputPath) {
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            HtmlConverter.convertToPdf(htmlContent, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving HTML to PDF", e);
        }
    }
}
