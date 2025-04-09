package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.*;
import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import com.boparty.bopartycatering.Services.OrdersService;

import com.boparty.bopartycatering.Services.ShoppingListService;
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
import org.yaml.snakeyaml.util.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrdersController {

    private final FrameworkServlet frameworkServlet;
    private final ShoppingListService shoppingListService;
    private OrdersService ordersService;



    @Autowired
    public OrdersController(OrdersService ordersService, FrameworkServlet frameworkServlet, ShoppingListService shoppingListService) {
        this.ordersService = ordersService;
        this.frameworkServlet = frameworkServlet;
        this.shoppingListService = shoppingListService;
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
    public String generatePdf(HttpServletResponse response, @PathVariable Long id, String color) {
        String filename = ordersService.getOrderFileName(id);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        // Create a new document
        Document document = new Document();

        try (OutputStream out = response.getOutputStream()) {
           ordersService.GeneratePdf(document,out,id,color);

           return "redirect:/order/view/"+id;
        } catch (Exception e) {
            return "redirect:/";

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
            newOrd.setStatus(order.getStatus());
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

    @GetMapping("/order/shopping/{id}")
    public String shopping(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            ShoppingList list = shoppingListService.getShoppingListByOrderId(id);

            if(list == null || list.isNeedsUpdate()){
                List<IngredientAmount> ings = ordersService.getShopping(List.of(order));
                list = shoppingListService.createList(order,ings);
            }

            model.addAttribute("shoppingList",list);
            Map<String, List<ShoppingListItem>> res = list.getItems()
                    .stream().collect(Collectors.groupingBy(x->x.getIngredient().getIngredient().getIngCategory().getName()));
            model.addAttribute("ingredients",res);

        }
        return "Order/shopping";
    }

    @PostMapping("/order/shopping/changeState/{ingId}")
    public ResponseEntity<Boolean> changeState(@PathVariable Long ingId, Model model) {
        ShoppingListItem item = shoppingListService.getItemById(ingId);
        if(item != null){
            item.setBought(!item.isBought());
            shoppingListService.saveItem(item);
            return ResponseEntity.ok(item.isBought());
        }
        return ResponseEntity.ok(false);
    }
    @PostMapping("/order/shopping/getState/{shoppingId}")
    public ResponseEntity<long[]> getState(Model model, @PathVariable Long shoppingId){
        return ResponseEntity.ok(shoppingListService.findSelectedItems(shoppingId));
    }



    @GetMapping("/order/shopping/collect")
    public String collect(long[] orderIds, Model model) {
        Orders temp = ordersService.createTempOrder(orderIds);
        return "redirect:/order/shopping/"+temp.getId();
    }

    @PostMapping("/order/changeStatus/{id}")
    public ResponseEntity<StatusResponse> changeStatus(@PathVariable Long id, @RequestParam Status status, Model model) {
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            order.setStatus(status);
            ordersService.save(order);
            return ResponseEntity.ok(new StatusResponse(order.getStatus(),order.getStatus().getColor()));
        }
        return ResponseEntity.ok(null);
    }

}
