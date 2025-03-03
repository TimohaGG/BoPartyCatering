package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.AmountUnit;
import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.ShoppingList;
import com.boparty.bopartycatering.Models.Position.*;
import com.boparty.bopartycatering.Models.User.CalendarQuickstart;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Services.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
public class MainController {


    private final OrdersService ordersService;
    private final PositionsService positionsService;
    private final UserService userService;
    private final ShoppingListService shoppingListService;
    private Orders tmpOrder;
    private List<PositionAmount> tmpPositions;
    private Map<Long,Integer> selectedIds;
    @Autowired
    public MainController(OrdersService ordersService, PositionsService positionsService, UserService userService, ShoppingListService shoppingListService) {
        this.ordersService = ordersService;
        this.positionsService = positionsService;
        this.userService = userService;
        tmpOrder = new Orders();
        tmpPositions = new ArrayList<>();
        selectedIds  = new HashMap<>();
        this.shoppingListService = shoppingListService;
    }
    @GetMapping("/")
    public String index(Model model) {
        List<Orders> orders = ordersService.getAllOrders();
        model.addAttribute("orders",ordersService.getAllOrders());
        model.addAttribute("tempOrders",ordersService.getTempOrders());
        tmpOrder = new Orders();
        tmpPositions = new ArrayList<>();
        selectedIds = new HashMap<>();


        try{
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            com.google.api.services.calendar.Calendar service =
                    new Calendar.Builder(HTTP_TRANSPORT, CalendarQuickstart.JSON_FACTORY, CalendarQuickstart.getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(CalendarQuickstart.APPLICATION_NAME)
                            .build();

            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            if (items.isEmpty()) {
                System.out.println("No upcoming events found.");
            } else {
                System.out.println("Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    System.out.printf("%s (%s)\n", event.getSummary(), start);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

       // List<IngredientAmount> res = ordersService.getShopping(new ArrayList<>());
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

            ShoppingList list = shoppingListService.getShoppingListByOrderId(order.getId());
            if(list!=null){
                list.setNeedsUpdate(true);
                shoppingListService.save(list);
            }

            if(tm.getShoppingList()!=null){
                tm.getShoppingList().setNeedsUpdate(true);
            }
            ordersService.save(tm);
        }

        catch (Exception e){
            System.out.printf(e.getMessage());
        }
        return "redirect:/";
    }


    @GetMapping("/positions/add")
    public String addPosition(Long categoryId, Model model) {
        Orders ord = (Orders)model.getAttribute("order");

        List<Category> categories = userService.getCategories();
        model.addAttribute("categories", categories);


        Category category;
        if(categoryId==null){
            category = categories.get(0);
        }
        else{
            category = categories.stream().filter(x->x.getId().equals(categoryId)).findFirst().orElse(null);
        }
        List<Position> positions = userService.getPositions();
        model.addAttribute("positions", positions.stream().filter(x->x.getCategory().getName().equals(category.getName())).toList());
        model.addAttribute("categoryName", category.getName());


        model.addAttribute("selected", selectedIds);
        model.addAttribute("selectedPositions", tmpPositions);

        model.addAttribute("order", ord);


        return "Order/addPositions";
    }


    //fetch
    @PostMapping("/positions/getTotalPrice")
    public ResponseEntity<Integer> getTotalPrice(){
        return ResponseEntity.ok(tmpPositions.stream().mapToInt(x-> (int) (x.getAmount() * x.getPosition().getPrice())).sum());
    }

    //fetch
    @GetMapping("/positions/addPosition")
    public String addPosition(Long positionId, int amount, Model model) {
        Position pos = positionsService.getPositionById(positionId);
        if(pos == null) {
            return "";
        }

        PositionAmount tmp;

        if(tmpPositions.stream().filter(x->x.getPositionId()==positionId).count()!=0) {
            tmp = tmpPositions.stream().filter(x->x.getPositionId()==positionId).findFirst().get();
            tmp.setAmount(amount);
        }
        else{
            tmp = new PositionAmount(pos,amount);
            tmpPositions.add(tmp);
        }
        if(selectedIds!=null)
            selectedIds = tmpPositions.stream().collect( Collectors.toMap(x->x.getPositionId(),PositionAmount::getAmount));
        ResponsePosAmount res = new ResponsePosAmount(amount,tmp.getPositionId(),tmp.getPosName(),tmp.getPosition().getPriceInt());
        model.addAttribute("pos", res);
        return "fragments/_selectedItem :: selectedItem";
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


//    @GetMapping("/order/generateShopping/{id}")
//    public String generateShopping(Model model, @PathVariable String id) {
//
//    }







}
