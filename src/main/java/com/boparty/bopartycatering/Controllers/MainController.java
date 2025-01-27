package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Models.Position.Position;
import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Services.OrdersService;
import com.boparty.bopartycatering.Services.PositionsService;
import com.boparty.bopartycatering.Services.UserService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {


    private OrdersService ordersService;
    private PositionsService positionsService;
    private Orders tmpOrder;
    private List<PositionAmount> tmpPositions;
    private Map<Long,Integer> selectedIds;
    @Autowired
    public MainController(OrdersService ordersService, PositionsService positionsService) {
        this.ordersService = ordersService;
        this.positionsService = positionsService;
        tmpOrder = new Orders();
        tmpPositions = new ArrayList<>();
        selectedIds  = new HashMap<>();

    }
    @GetMapping("/")
    public String index(Model model) {
        List<Orders> orders = ordersService.getAllOrders();
        for (Orders order : orders) {
            System.out.println(order.getPositionsAmount());
        }
        model.addAttribute("orders",ordersService.getAllOrders());
        tmpOrder = new Orders();
        tmpPositions = new ArrayList<>();
        selectedIds = new HashMap<>();
        return "index";
    }

    @GetMapping("/create/order")
    public String createOrder(Model model) {
        //Orders order = new Orders();
        model.addAttribute("order", tmpOrder);
        model.addAttribute("selectedPositions", tmpPositions);
        return "Order/orderCreate";
    }

    @PostMapping("/create/order")
    public String createOrder(@ModelAttribute("order") Orders order,RedirectAttributes attributes, Model model) {
        attributes.addFlashAttribute("order", order);
        tmpOrder = order;
        return "redirect:/positions/add";
    }


    @PostMapping("/save/order")
    public String saveOrder(@ModelAttribute("order") Orders order,RedirectAttributes attributes, Model model) {

        try{

            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            order.setUser(user);
            Orders tm =  ordersService.save(order);
            positionsService.removeZeroPositions(order.getId(), tmpPositions);
            positionsService.saveAll(tmpPositions);
            tmpPositions.forEach(el->{el.setOrder(tm);});
            tm.setPositionsAmount(tmpPositions);
            ordersService.save(tm);
        }

        catch (Exception e){
            System.out.printf(e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/positions/add")
    public String addPosition(Long categoryId,  Model model) {
        Orders ord = (Orders)model.getAttribute("order");

        List<Category> categories = positionsService.getCategories();
        model.addAttribute("categories", categories);

        List<Position> positions = positionsService.getPositions();
        Category category;
        if(categoryId==null){
            category = categories.get(0);
        }
        else{
            category = categories.stream().filter(x->x.getId().equals(categoryId)).findFirst().orElse(null);
        }
        model.addAttribute("positions", positions.stream().filter(x->x.getCategory().getName().equals(category.getName())).toList());
        model.addAttribute("categoryName", category.getName());


        model.addAttribute("selected", selectedIds);
        model.addAttribute("selectedPositions", tmpPositions);

        model.addAttribute("order", ord);


        return "Order/addPositions";
    }


    //fetch
    @GetMapping("/positions/addPosition")
    public ResponseEntity<Integer> addPosition(Long positionId, int amount) {
        Position pos = positionsService.getPositionById(positionId);
        if(pos == null) {
            return ResponseEntity.ok(0);
        }

        if(tmpPositions.stream().filter(x->x.getPositionId()==positionId).count()!=0) {
            tmpPositions.stream().filter(x->x.getPositionId()==positionId).findFirst().get().addAmount(amount);
        }
        else{
            tmpPositions.add(new PositionAmount(pos,amount));
        }
        if(selectedIds!=null)
            selectedIds = tmpPositions.stream().collect( Collectors.toMap(x->x.getPositionId(),PositionAmount::getAmount));
        return ResponseEntity.ok(amount);
    }
    //fetch
    @GetMapping("/positions/remove/{id}")
    public ResponseEntity<Boolean> removePosition(@PathVariable Long id, Model model) {
        PositionAmount pos = tmpPositions.stream().filter(x->x.getPositionId()==id).findFirst().orElse(null);
        if(pos != null) {
            tmpPositions.remove(pos);
            selectedIds.remove(pos.getPositionId());
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);


    }

    @GetMapping("/edit/order/{orderId}")
    public String editOrder(@PathVariable long orderId, Model model) {
        Orders order = ordersService.getOrderById(orderId);
        if(order == null) {
            return "redirect:/";
        }

        tmpOrder = order;
        tmpPositions = order.getPositionsAmount();

        model.addAttribute("order", order);
        model.addAttribute("selectedPositions", order.getPositionsAmount());
        selectedIds = tmpPositions.stream().collect( Collectors.toMap(x->x.getPositionId(),PositionAmount::getAmount));
        return "Order/orderCreate";

    }



}
