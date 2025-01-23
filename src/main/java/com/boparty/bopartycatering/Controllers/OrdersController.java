package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.InfoDTO;
import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.PdfGenerator;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import com.boparty.bopartycatering.Services.OrdersService;
import com.boparty.bopartycatering.Services.PdfService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
public class OrdersController {

    private OrdersService ordersService;
    private PdfService pdfService;


    @Autowired
    public OrdersController(OrdersService ordersService, PdfService pdfService) {
        this.ordersService = ordersService;
        this.pdfService = pdfService;
    }
@GetMapping("/order/view/{id}")
    public String index(@PathVariable long id, Model model){
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            model.addAttribute("order", order);
            model.addAttribute("info", new InfoDTO());
            return "Order/index";
        }
        return "redirect:/";
    }

    @PostMapping("/order/generate/{id}")
    public void generatePdf(HttpServletResponse response, @PathVariable Long id) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=example.pdf");

        // Create a new document
        Document document = new Document();

        try (OutputStream out = response.getOutputStream()) {
           ordersService.GeneratePdf(document,out,id);


        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }

    @PostMapping("/order/addinfo/{id}")
    public String addInfo(@PathVariable String id,@ModelAttribute InfoDTO infoDTO, Model model) {
        return "redirect:/";
    }




}
