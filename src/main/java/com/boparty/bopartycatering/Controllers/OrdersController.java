package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.InfoDTO;
import com.boparty.bopartycatering.Models.Order.OrderAdditionalInfo;
import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.PdfGenerator;
import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import com.boparty.bopartycatering.Services.OrdersService;

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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.FrameworkServlet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class OrdersController {

    private final FrameworkServlet frameworkServlet;
    private OrdersService ordersService;



    @Autowired
    public OrdersController(OrdersService ordersService, FrameworkServlet frameworkServlet) {
        this.ordersService = ordersService;
        this.frameworkServlet = frameworkServlet;
    }
    @GetMapping("/order/view/{id}")
    public String index(@PathVariable long id, Model model){
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            model.addAttribute("order", order);
            model.addAttribute("info", new InfoDTO());
            model.addAttribute("common",ordersService.getCommonAdditionalInfo());
            return "Order/index";
        }
        return "redirect:/";
    }

    @GetMapping("/order/remove/{id}")
    public String remove(@PathVariable long id, Model model){
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            ordersService.removeOrder(id);
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
    public String addInfo(@PathVariable Long id, @ModelAttribute InfoDTO infoDTO, Model model) {


        OrderAdditionalInfo tmp = new OrderAdditionalInfo();
        tmp.setTitle(infoDTO.getTitle());
        tmp.setDescription(infoDTO.getDescription());
        tmp.setPrice(infoDTO.getPrice());
        tmp.setCommon(infoDTO.getSave());
        try{
            tmp.setImage(infoDTO.getImage().getBytes());
        }catch (Exception ex){

        }
        ordersService.addAdditionalInfo(id,tmp);
        return "redirect:/order/view/" + id;
    }

    @PostMapping("/order/deleteInfo/{id}")
    public String deleteInfo(@PathVariable Long id,Long orderId, Model model) {
        ordersService.removeAdditionalInfo(id);
        return "redirect:/order/view/" + orderId;
    }


    @GetMapping("/order/copy/{id}")
    public String copy(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            Orders newOrd = new Orders();
            newOrd.setClient(order.getClient() + "(copy)");
            newOrd.setUser(order.getUser());
            newOrd.setDate(order.getDate());
            newOrd.setDuration(order.getDuration());
            newOrd.setFormat(order.getFormat());
            newOrd.setPhone(order.getPhone());
            newOrd.setGuestsAmount(order.getGuestsAmount());
            newOrd = ordersService.save(newOrd);
            for(PositionAmount pos : order.getPositionsAmount()){
                PositionAmount p = new PositionAmount();
                p.setAmount(pos.getAmount());
                p.setPosition(pos.getPosition());
                newOrd.addPosition(p);
                //newOrd.getPositionsAmount().add(p);
                p.setOrder(newOrd);
                ordersService.savePositionAmount(p);
            }

            ordersService.save(newOrd);



            for (OrderAdditionalInfo info : order.getAdditionalInfo()) {
                OrderAdditionalInfo tmp = new OrderAdditionalInfo();
                tmp.setTitle(info.getTitle());
                tmp.setDescription(info.getDescription());
                tmp.setPrice(info.getPrice());
                tmp.setImage(info.getImage());
                tmp.setCommon(false);
                tmp.setOrder(newOrd);
                ordersService.saveInfo(tmp);
            }

        }
        return "redirect:/";
    }


    @PostMapping("/info/get/{id}")
    public ResponseEntity<InfoDTO> getInfo(@PathVariable Long id, Model model) {
        OrderAdditionalInfo tmp = ordersService.getCommonInfoById(id);
        InfoDTO dto = new InfoDTO();
        if(tmp != null){

            dto.setTitle(tmp.getTitle());
            dto.setDescription(tmp.getDescription());
            dto.setPrice(tmp.getPrice());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.ok(dto);
    }


}
