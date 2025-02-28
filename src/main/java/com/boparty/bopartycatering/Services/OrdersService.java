package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Order.AmountUnit;
import com.boparty.bopartycatering.Models.Order.OrderAdditionalInfo;
import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.PdfGenerator;
import com.boparty.bopartycatering.Models.Position.Ingredient;
import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import com.boparty.bopartycatering.Models.Position.Position;
import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Repos.IAdditionalInfoRepos;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import com.boparty.bopartycatering.Repos.PositionAmountRepos;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdersService {
    private final OrdersRepos ordersRepos;
    private final IAdditionalInfoRepos iAdditionalInfoRepos;
    private final PositionAmountRepos positionAmountRepos;
    private UserService userService;

    @Autowired
    public OrdersService(OrdersRepos ordersRepos, IAdditionalInfoRepos iAdditionalInfoRepos, UserService userService, PositionAmountRepos positionAmountRepos) {
        this.ordersRepos = ordersRepos;
        this.iAdditionalInfoRepos = iAdditionalInfoRepos;
        this.userService = userService;
        this.positionAmountRepos = positionAmountRepos;

    }

    public List<Orders> getAllOrders() {

        String username = userService.getCurrentUser().getUsername();
        return ordersRepos.findAll().stream().filter(x->x.getUser().getUsername().equals(username)).sorted((order1, order2) -> order2.getDate().compareTo(order1.getDate())).toList();
    }

    public Orders save(Orders orders) {
        return ordersRepos.save(orders);
    }

    public Orders getOrderById(Long id) {
        return ordersRepos.findById(id).orElse(null);
    }



    public PdfWriter GeneratePdf(Document document, OutputStream out, long id, String backColor) throws DocumentException {
        PdfGenerator generator = new PdfGenerator(getOrderById(id));


        PdfGenerator.backgroundColor = BaseColor.WHITE;
        PdfGenerator.fontColor = BaseColor.BLACK;
        PdfGenerator.containerColor = BaseColor.WHITE;
        if(backColor.isEmpty()){
            PdfGenerator.posHeaderColor = new BaseColor(250,187,7);
        }
        else{
            String[] res = backColor.split(",");
            int[] color = Arrays.stream(backColor.split(",")).mapToInt(x->Integer.parseInt(x.trim())).toArray();
            PdfGenerator.posHeaderColor = new BaseColor(color[0],color[1],color[2]);
        }

        PdfGenerator.summaryHeaderColor = new BaseColor(91,91,91);

        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Open the document for writing
        document.open();


        generator.generate(document);

        document.close();
        writer.flush();
        return writer;
    }

    public void addAdditionalInfo(Long orderId, OrderAdditionalInfo info) {
        Orders orders = ordersRepos.findById(orderId).orElse(null);
        if (orders!=null){
            info.setOrder(orders);
            iAdditionalInfoRepos.save(info);
        }

    }

    public void removeAdditionalInfo(Long id) {
        OrderAdditionalInfo inf = iAdditionalInfoRepos.findById(id).orElse(null);
        if (inf!=null){
            if(inf.isCommon()){
                inf.setOrder(null);
                iAdditionalInfoRepos.save(inf);
            }
            else{
                iAdditionalInfoRepos.delete(inf);
            }
        }
    }

    public void removeOrder(Long id) {
        Orders orders = ordersRepos.findById(id).orElse(null);
        if (orders!=null){
            if(orders.getAdditionalInfo()!=null){
                iAdditionalInfoRepos.deleteAll(orders.getAdditionalInfo());
            }
            if(orders.getPositionsAmount()!=null){
                positionAmountRepos.deleteAll(orders.getPositionsAmount());
            }
        }
        ordersRepos.deleteById(id);



    }

    public void saveInfo(OrderAdditionalInfo info) {
        iAdditionalInfoRepos.save(info);
    }

    public void savePositionAmount(PositionAmount pos) {
        positionAmountRepos.save(pos);
    }

    public List<OrderAdditionalInfo> getCommonAdditionalInfo(){
        return iAdditionalInfoRepos.findAll().stream().filter(OrderAdditionalInfo::isCommon).toList();
    }

    public OrderAdditionalInfo getCommonInfoById(Long id) {
        OrderAdditionalInfo tmp = iAdditionalInfoRepos.findById(id).orElse(null);
        if(tmp==null){
            return null;
        }
        if(tmp.isCommon()){
            return tmp;
        }
        return null;
    }

    public String getOrderFileName(long id){
        Orders tmp = ordersRepos.findById(id).orElse(null);
        if(tmp==null){
            return "";
        }
        return tmp.getClient() + " " + tmp.getDate()+".pdf";
    }

    public List<IngredientAmount> getShopping(List<Orders> orders) {

        //orders = List.of(ordersRepos.getOrderById(74L), ordersRepos.getOrderById(75L));

        // Combine all orders into one list with summed amounts
        List<PositionAmount> positions = orders.stream()
                .flatMap(order -> order.getPositionsAmount().stream())
                .collect(Collectors.groupingBy(
                        PositionAmount::getPosition, Collectors.summingInt(PositionAmount::getAmount)))
                .entrySet().stream()
                .map(e -> new PositionAmount(e.getKey(), e.getValue()))
                .toList();

        List<IngredientAmount> ings = positions.stream()
                .flatMap(pos -> pos.getPosition().getIngredients().stream()
                        .map(ingAm -> new IngredientAmount(ingAm.getIngredient(),
                                ingAm.getAmount() * pos.getAmount(), ingAm.getUnit())))
                .collect(Collectors.toMap(
                        ing->ing.getIngredient().getId(),
                        item->item,
                        (existIt,newIt)->new IngredientAmount(existIt.getId(), existIt.getIngredient(),existIt.getUnit(),existIt.getAmount() + newIt.getAmount(), existIt.getPosition())
                ))
                .values().stream().toList();

        ings.forEach(v -> System.out.println(v.getIngredient().getName() + " " + v.getAmount() + " " + v.getUnit().getUnitName()));


        return ings;
    }
}
