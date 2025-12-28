package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Models.Position.Ingredient;
import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import com.boparty.bopartycatering.Models.Position.Position;
import com.boparty.bopartycatering.Repos.CategoriesRepos;
import com.boparty.bopartycatering.Repos.IIngAmountRepos;
import com.boparty.bopartycatering.Repos.PositionsRepos;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.spire.pdf.utilities.PdfTable;
import jakarta.annotation.Nullable;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGeneratorService {


    private final PositionsRepos posRepos;
    private final CategoriesRepos categoriesRepos;
    private final Font defaultFont;
    private final Font headerFont;
    @Autowired
    public PdfGeneratorService(PositionsRepos posRepos, CategoriesRepos categoriesRepos, Font defaultFont, Font headerFont){
        this.posRepos = posRepos;
        this.categoriesRepos = categoriesRepos;
        this.defaultFont = defaultFont;
        this.headerFont = headerFont;
    }

    public byte[] generate(long categoryId){
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Category category = categoriesRepos.findById(categoryId).orElse(null);
            if(category!=null) document.add(generateHeaderCell(category.getName()));

            PdfPTable table = generateTable(getPositionsList(categoryId));
            document.add(table);
            document.close();
            return out.toByteArray();

        }catch (Exception e){
            return null;
        }
    }

    private List<Position> getPositionsList(long categoryId){
        List<Position> positions = null;
        if(categoryId == 0){
            long id = UserService.getCurrent().getId();
            positions = this.posRepos.findAllByCategory_User_id(id);
        }
        else{
            positions = this.posRepos.findAllByCategoryId(categoryId);
        }
        return positions;
    }

    private PdfPTable generateTable(List<Position> positions){
        try{


            PdfPTable table = new PdfPTable(new float[]{30,30,30});
            table.setWidthPercentage(100);
            for(Position position : positions){

                table.addCell(generateCell(position.getName(), Element.ALIGN_CENTER));

                if(position.getImage() != null){
                    Image img = Image.getInstance(position.getImage());
                    img.scaleToFit(25,25);
                    table.addCell(img);
                }else{
                    table.addCell(" ");
                }


                StringBuilder string = new StringBuilder();
                for (IngredientAmount ingredient : position.getIngredients()){
                    string.append(" - ").append(ingredient.getIngredient().getName()).append(" ");
                    string.append(ingredient.getAmount()).append(" ");
                    string.append(ingredient.getUnit().getUnitName()).append("\n");
                }

                table.addCell(generateCell(string.toString(), Element.ALIGN_LEFT));
            }
            return table;
        }catch (Exception e){
            return null;
        }
    }

    private Paragraph generateHeaderCell(String text){
        Phrase phrase = new Phrase(text,headerFont);
        Paragraph paragraph = new Paragraph(phrase);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(15);
        return paragraph;
    }

    private PdfPCell generateCell(String text, int alignment){
        Phrase phrase = new Phrase(text,defaultFont);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(alignment);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);
        cell.setPaddingLeft(5);
        cell.setPaddingRight(5);
        return cell;
    }
}
