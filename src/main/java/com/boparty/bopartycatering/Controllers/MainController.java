package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.DTOs.OrderPaginationDto;
import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.ShoppingList;
import com.boparty.bopartycatering.Models.Order.Status;
import com.boparty.bopartycatering.Models.Position.*;

import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Services.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.*;
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
    private final GoogleOAuthService googleOAuthService;
    private final GoogleCalendarService calendarService;
    private final PdfGeneratorService pdfGeneratorService;

    private final ErrorAttributes errorAttributes;



    @Autowired
    public MainController(OrdersService ordersService, PositionsService positionsService, UserService userService, ShoppingListService shoppingListService, GoogleOAuthService googleOAuthService, GoogleCalendarService calendarService, PdfGeneratorService pdfGeneratorService, ErrorAttributes errorAttributes) {
        this.ordersService = ordersService;
        this.positionsService = positionsService;
        this.userService = userService;
        this.pdfGeneratorService = pdfGeneratorService;
        tmpOrder = new Orders();
        tmpPositions = new ArrayList<>();
        selectedIds  = new HashMap<>();
        this.shoppingListService = shoppingListService;
        this.googleOAuthService = googleOAuthService;
        this.calendarService = calendarService;
        this.errorAttributes = errorAttributes;


    }
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(defaultValue = "1", required = false) Long pageNumber,
                        @RequestParam(defaultValue = "10", required = false) Long amount,
                        @RequestParam(defaultValue = "",required = false) String searchValue) {
        OrderPaginationDto orders = ordersService.getAllOrders(10,pageNumber, searchValue);
        model.addAttribute("orders",orders.getOrdersList());
        model.addAttribute("totalPages", (int) Math.ceil((double) orders.getTotalAmount()/amount));
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("maxPaginationButtonsAmount", 5);

        model.addAttribute("tempOrders",ordersService.getTempOrders());
        model.addAttribute("statuses", Status.values());
        try{
            model.addAttribute("authorized",googleOAuthService.isUserAuthorized(userService.getCurrentUser().getUsername()));
            model.addAttribute("calendars",googleOAuthService.getAllCalendars(userService.getCurrentUser().getUsername()));
        }catch (Exception e){
            model.addAttribute("authorized",false);
        }



        tmpOrder = new Orders();
        tmpPositions = new ArrayList<>();
        selectedIds = new HashMap<>();

        return "index";
    }

    @GetMapping("/oauth2/authorize")
    public String authorize() {
        String res = googleOAuthService.getAuthorizationUrl();
        return "redirect:"+googleOAuthService.getAuthorizationUrl();
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam("code") String code, HttpServletRequest request) {
        try {
            Credential credential = googleOAuthService.getCredentials(code, userService.getCurrentUser().getUsername());
            Calendar calendarService = googleOAuthService.getCalendarService(userService.getCurrentUser().getUsername());
            request.getSession().setAttribute("calendarService", calendarService);
            return "redirect:/";
        } catch (IOException e) {
            return "redirect:/error";
        }
    }

    @GetMapping("/oauth2/logout")
    public String logout() {
        try{
            googleOAuthService.logoutUser(userService.getCurrentUser().getUsername());
        }catch (Exception e){
            System.out.println("Error logging out");
        }
        return "redirect:/";
    }

    @GetMapping("/order/addCalendar/{id}")
    public ResponseEntity<Boolean> addCalendar(Model model, @PathVariable Long id) {
        Orders order = ordersService.getOrderById(id);
        if(order != null){
            User user = userService.getCurrentUser();
            if(calendarService.createEvent(user.getUsername(),order, user.getDefaultCalendar(), user.getDefaultColor())){
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.ok(false);

        }
        return ResponseEntity.ok(false);
    }



    @GetMapping("/user/changeDefCalendar")
    public ResponseEntity<String> changeDefCalendar(@RequestParam String calendar, Model model) {
        User user = userService.getCurrentUser();
        user.setDefaultCalendar(calendar);
        userService.save(user);
        return ResponseEntity.ok(calendar);
    }

    @GetMapping("/user/changeDefColor")
    public ResponseEntity<String> changeDefColor(@RequestParam String color, Model model) {
        User user = userService.getCurrentUser();
        user.setDefaultColor(color);
        userService.save(user);
        return ResponseEntity.ok(color);
    }

    @GetMapping("/settings")
    public String settings(Model model) {

        User user = userService.getCurrentUser();
        model.addAttribute("defCalendar",user.getDefaultCalendar());
        model.addAttribute("defColor",user.getDefaultColor());
        try{
            model.addAttribute("authorized",googleOAuthService.isUserAuthorized(user.getUsername()));
            model.addAttribute("calendars",googleOAuthService.getAllCalendars(user.getUsername()));
            model.addAttribute("colors",googleOAuthService.getColors(user.getUsername()));
        }catch (Exception e){
            model.addAttribute("authorized",false);
        }
        return "User/settings";
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
            if(order.getId() != null){
                ordersService.removePositions(order.getId());
                this.tmpPositions.forEach(PositionAmount::removeId);
            }
            Orders tm =  ordersService.save(order);

            this.tmpPositions.removeIf(pos->pos.getAmount()==0);

//            positionsService.removeZeroPositions(order.getId(), tmpPositions);
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
            if(!categories.isEmpty())
                category = categories.get(0);
            else
                return "redirect:/error";
        }
        else{
            category = categories.stream().filter(x->x.getId().equals(categoryId)).findFirst().orElse(null);
        }
//        List<Position> positions = userService.getPositions();
        model.addAttribute("positions", positionsService.getPositionsByCategory(category));
        model.addAttribute("categoryName", category.getName());


        model.addAttribute("selected", selectedIds);
        model.addAttribute("selectedPositions", tmpPositions);

        model.addAttribute("order", ord);


        return "Order/addPositions";
    }

    @GetMapping("/positions/search")
    public String addPositionSearch(@RequestParam String searchVal, Model model) {

        List<Category> categories = userService.getCategories();
        model.addAttribute("categories", categories);

        List<Position> positions = positionsService.getPositionsByNamePart(searchVal);
        model.addAttribute("positions", positions);
        model.addAttribute("categoryName", searchVal);


        model.addAttribute("selected", selectedIds);
        model.addAttribute("selectedPositions", tmpPositions);


        model.addAttribute("searchVal", searchVal);


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
        tmpPositions = new ArrayList<>(order.getPositionsAmount());

        model.addAttribute("order", order);
        model.addAttribute("selectedPositions", order.getPositionsAmount());

//        tmpPositions.stream().collect(Collectors.groupingBy(PositionAmount::getPositionId));
        selectedIds = tmpPositions.stream().collect( Collectors.toMap(x->x.getPositionId(),PositionAmount::getAmount));
        return "Order/orderCreate";

    }

    @GetMapping("/parse")
    public String parse(Model model) {
        return "Order/parseDocument";
    }

    @PostMapping("/parse")
    public String parseDocument(MultipartFile menu, Model model) {
        List<String> errors = new ArrayList<>();
        Orders order = null;
        List<String[]> cells = this.ordersService.parseOrder(menu);

        order = this.ordersService.createOrderDetailsFromText(cells,errors);
        List<PositionAmount> positions = this.positionsService.parsePositions(cells, order,errors);
        order.setPositionsAmount(positions);
        ordersService.save(order);

        model.addAttribute("errors", errors);
        model.addAttribute("orderId", order.getId());
        return "Order/parseResult";
//        return order==null ?"redirect:/": "redirect:/order/view/"+order.getId();
    }


//    @GetMapping("/order/generateShopping/{id}")
//    public String generateShopping(Model model, @PathVariable String id) {
//
//    }


    @GetMapping("/pos-ings/view")
    public String PosingsView(Model model){

        List<Category>  categories = userService.getCategories();
        model.addAttribute("categories", categories);
        return "Positions/PdfView";
    }


    @PostMapping("/pos-ings/generate")
    public ResponseEntity<byte[]> generatePosings(Model model, Long categorySelect) {
        byte[] pdfBytes = this.pdfGeneratorService.generate(categorySelect);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "file.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }








}
