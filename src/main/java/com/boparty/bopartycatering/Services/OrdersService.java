package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.PdfGenerator;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersService {
    private OrdersRepos ordersRepos;
    @Autowired
    public OrdersService(OrdersRepos ordersRepos) {
        this.ordersRepos = ordersRepos;
    }

    public List<Orders> getAllOrders() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        return ordersRepos.findAll().stream().filter(x->x.getUser().getUsername().equals(username)).toList();
    }

    public Orders save(Orders orders) {
        return ordersRepos.save(orders);
    }

    public Orders getOrderById(Long id) {
        return ordersRepos.findById(id).orElse(null);
    }


    public PdfWriter GeneratePdf(Document document, OutputStream out, long id) throws DocumentException {
        PdfGenerator generator = new PdfGenerator(getOrderById(id));


        PdfGenerator.backgroundColor = BaseColor.WHITE;
        PdfGenerator.fontColor = BaseColor.BLACK;
        PdfGenerator.containerColor = BaseColor.WHITE;
        PdfGenerator.posHeaderColor = new BaseColor(250,187,7);

        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Open the document for writing
        document.open();


        generator.generate(document);

        document.close();
        writer.flush();
        return writer;
    }
}
