package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PdfGenerator {
    private final Orders order;
    Document document;
    Font mainFont;
    Font boldFont;
    Font blackBoldFont;


    public static BaseColor backgroundColor;
    public static BaseColor fontColor;
    public static BaseColor containerColor;
    public static BaseColor posHeaderColor;
    public static BaseColor summaryHeaderColor;

    Map<String,String> header = new LinkedHashMap<>();
    String posMainHeader = "Позиції";
    List<String> posHeader = new ArrayList<>();
    String summaryHeader = "Загалом";

    public PdfGenerator(Orders order) {
        this.order = order;

        header.put("Замовник",order.getClient());
        header.put(   "Дата",order.getDate());
        header.put(   "Початок заходу",order.getDate());
        header.put(   "Тривалість",String.valueOf(order.getDuration()));
        header.put(  "К-сть запрошених",String.valueOf( order.getGuestsAmount()));
        header.put(  "Формат заходу",order.getFormat());
        header.put( "Телефон відповідального менеджера",order.getPhone());

        posHeader.add("Найменування");
        posHeader.add("");
        posHeader.add("Вихід, \nграм");
        posHeader.add("К-сть порцій");
        posHeader.add("Ціна, \nгрн");


        try (InputStream fontStream = new ClassPathResource("static/asserts/fonts/Arial Unicode.ttf").getInputStream()) {
            BaseFont baseFont = BaseFont.createFont("Arial Unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);
            mainFont = new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK);
        } catch (IOException | DocumentException e) {
            System.out.println("Error creating font");
        }

        // Load the bold font
        try (InputStream boldFontStream = new ClassPathResource("static/asserts/fonts/ArialMT.ttf").getInputStream()) {
            BaseFont boldBaseFont = BaseFont.createFont("ArialMT.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, boldFontStream.readAllBytes(), null);
            boldFont = new Font(boldBaseFont, 20, Font.BOLD, BaseColor.WHITE);
            blackBoldFont = new Font(boldBaseFont, 16, Font.BOLD, BaseColor.BLACK);
        } catch (DocumentException | IOException e) {
            System.out.println("Error creating font");
        }

    }

    public void generate(Document document) {
        try {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            addTableHeader(table);

            PdfPTable positions = new PdfPTable(5);
            positions.setWidthPercentage(100);
            addPositionsHeader(positions);
            addPositionsCell(positions);

            PdfPTable summary = new PdfPTable(3);
            summary.setWidthPercentage(100);
            addSummary(summary);

            document.add(table);
            document.add(positions);
            document.add(summary);
        } catch (DocumentException e) {
            System.out.println("There was an error generating the document");
        }
    }


    private void addTableHeader(PdfPTable table) {
        AtomicBoolean isImgSet = new AtomicBoolean(false);
        header
                .forEach((key, value) -> {
                    table.addCell(getDefaultCell(key, mainFont));
                    table.addCell(getDefaultCell(value, mainFont));

                    if(!isImgSet.get()) {
                        String logoPath;
                        try {
                            ClassPathResource classpath = new ClassPathResource("static/asserts/img/logo.png");
                            byte[] imageBytes;
                            try (InputStream inputStream = classpath.getInputStream()) {
                                imageBytes = inputStream.readAllBytes();
                            }
                            //logoPath = new ClassPathResource("static/asserts/img/logo.png").getFile().getAbsolutePath();
                            Image img = Image.getInstance(imageBytes);
                            img.scaleToFit(150,150);
                            PdfPCell cell = new PdfPCell(img);
                            cell.setRowspan(7);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            table.addCell(cell);

                            isImgSet.set(true);
                        } catch (Exception e) {
                            System.out.println("Error creating PDF");
                        }

                    }
                });
    }

    private void addPositionsHeader(PdfPTable table) throws DocumentException {
        PdfPCell tmp = getDefaultCell(posMainHeader, boldFont);
        tmp.setColspan(5);
        tmp.setBackgroundColor(posHeaderColor);
        tmp.setMinimumHeight(50);
        table.addCell(tmp);
        table.setWidths(new float[]{27.5f,27.5f,15f,15f,15f});
        posHeader.forEach((key) -> {
            PdfPCell cell = getDefaultCell(key, blackBoldFont);
            //cell.setBackgroundColor(posHeaderColor);
            table.addCell(cell);
        });
    }

    private void addPositionsCell(PdfPTable table) {
        for(PositionAmount pos : order.getPositionsAmount()){
            table.addCell(getDefaultCell(pos.getPositionName(), mainFont));

            try{
                Image img = Image.getInstance(pos.getPosition().getImage());
                img.scaleToFit(50,50);
                PdfPCell cell = new PdfPCell(img);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            catch(Exception e){
                table.addCell(new Paragraph(""));
            }


            table.addCell(getDefaultCell(String.valueOf(((int)pos.getPosition().getWeight())), mainFont));
            table.addCell(getDefaultCell(String.valueOf(pos.getAmount()), mainFont));
            table.addCell(getDefaultCell(String.valueOf(((int)pos.getPosition().getPrice())), mainFont));
        }

    }

    private void addSummary(PdfPTable table) {
        //header
        PdfPCell header = getDefaultCell(summaryHeader, boldFont);
        header.setBackgroundColor(summaryHeaderColor);
        header.setColspan(3);
        table.addCell(header);

        for (OrderAdditionalInfo info : order.getAdditionalInfo()) {
            table.addCell(getDefaultCell(info.getTitle(), mainFont));
            table.addCell(getDefaultCell(info.getDescription(), mainFont));
            if(info.getImage()!=null){
                try{
                    Image img = Image.getInstance(info.getImage());
                    img.scaleToFit(50,50);
                    table.addCell(img);
                }
                catch(Exception e){
                    table.addCell(new Paragraph(""));
                }
            }
            else{
                table.addCell(new Paragraph(""));
            }
        }


    }

    private PdfPCell getDefaultCell(String data, Font font){
        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPhrase(new Phrase(data, font));
        header.setMinimumHeight(40);
        header.setBackgroundColor(containerColor);
        return header;
    }




}
