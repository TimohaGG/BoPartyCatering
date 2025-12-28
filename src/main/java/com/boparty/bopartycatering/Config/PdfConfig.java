package com.boparty.bopartycatering.Config;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class PdfConfig {

    @Bean
    public Font defaultFont(){
        try (InputStream fontStream = new ClassPathResource("fonts/Arial Unicode.ttf").getInputStream()) {
            BaseFont baseFont = BaseFont.createFont("Arial Unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);
            return new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK);
        } catch (IOException | DocumentException e) {
            System.out.println("Error creating font");
            return null;
        }
    }


    @Bean
    public Font headerFont(){
        try (InputStream fontStream = new ClassPathResource("fonts/Arial Unicode.ttf").getInputStream()) {
            BaseFont baseFont = BaseFont.createFont("Arial Unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);
            return new Font(baseFont, 24, Font.NORMAL, BaseColor.BLACK);
        } catch (IOException | DocumentException e) {
            System.out.println("Error creating font");
            return null;
        }
    }


}
