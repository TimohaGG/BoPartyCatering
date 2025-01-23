package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.core.io.ClassPathResource;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PdfGenerator {
    private Orders order;
    Document document;
    Font mainFont;
    Font boldFont;

    public static BaseColor backgroundColor;
    public static BaseColor fontColor;
    public static BaseColor containerColor;
    public static BaseColor posHeaderColor;

    Map<String,String> header = new LinkedHashMap<>();
    String posMainHeader = "Позиції";
    List<String> posHeader = new ArrayList<>();

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




        try{
            String fontPath = new ClassPathResource("static/asserts/fonts/Arial Unicode.ttf").getFile().getAbsolutePath();
            mainFont = new Font(BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED),14, Font.NORMAL,fontColor);
            String fontBoldPath = new ClassPathResource("static/asserts/fonts/Arial Rounded Bold.ttf").getFile().getAbsolutePath();
            boldFont = new Font(BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED),20, Font.BOLD,BaseColor.WHITE);
        }catch (Exception e){
            System.out.println(e.getMessage());
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
            document.add(table);
            document.add(positions);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
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
                            logoPath = new ClassPathResource("static/asserts/img/logo.png").getFile().getAbsolutePath();
                            Image img = Image.getInstance(logoPath);
                            img.scaleToFit(150,150);
                            PdfPCell cell = new PdfPCell(img);
                            cell.setRowspan(7);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            table.addCell(cell);

                            isImgSet.set(true);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
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
            PdfPCell cell = getDefaultCell(key,mainFont);
            cell.setBackgroundColor(posHeaderColor);
            table.addCell(cell);
        });
    }

    private void addPositionsCell(PdfPTable table) {
        for(PositionAmount pos : order.getPositionsAmount()){
            table.addCell(getDefaultCell(pos.getPositionName(), mainFont));
            table.addCell(getDefaultCell("", mainFont));
            table.addCell(getDefaultCell(pos.getPosition().getWeight().toString(), mainFont));
            table.addCell(getDefaultCell(String.valueOf(pos.getAmount()), mainFont));
            table.addCell(getDefaultCell(String.valueOf(pos.getPosition().getPrice()), mainFont));
        }

    }

    private PdfPCell getDefaultCell(String data, Font font){
        PdfPCell header = new PdfPCell();


        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPhrase(new Phrase(data, font));
        header.setMinimumHeight(30);
        header.setBackgroundColor(containerColor);


        return header;
    }




}
