package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import com.boparty.bopartycatering.Services.OrdersService;
import com.boparty.bopartycatering.Services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
            return "Order/index";
        }
        return "redirect:/";
    }

    @PostMapping("/order/generate")
    public String generatePdf(@RequestBody String htmlContent) {
        String outputPath = "output.pdf"; // Path where the PDF will be saved
        pdfService.saveHtmlAsPdf(htmlContent, outputPath);
        return "redirect:/";
    }
}
