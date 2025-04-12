package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.ShoppingList;
import com.boparty.bopartycatering.Models.Order.ShoppingListItem;
import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import com.boparty.bopartycatering.Repos.IShoppingListItemRepos;
import com.boparty.bopartycatering.Repos.IShoppingListRepos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingListService {
    private final IShoppingListRepos shoppingListRepo;
    private final IShoppingListItemRepos shoppingListItemRepo;

    public ShoppingListService(IShoppingListRepos shoppingListRepo, IShoppingListItemRepos shoppingListItemRepo) {
        this.shoppingListRepo = shoppingListRepo;
        this.shoppingListItemRepo = shoppingListItemRepo;
    }

    public ShoppingList getShoppingListByOrderId(long id) {
        return shoppingListRepo.findShoppingListByOrderId(id);
    }

    public ShoppingList createList(Orders order, List<IngredientAmount> ingredients) {

        ShoppingList shoppingList = getShoppingListByOrderId(order.getId());
        if(shoppingList == null) {
            shoppingList = new ShoppingList();
        }
        shoppingList.setOrder(order);




        List<ShoppingListItem> items = new ArrayList<>();
        for(IngredientAmount ing : ingredients) {
            items.add(new ShoppingListItem(ing, shoppingList));
        }
//
        if(shoppingList.getItems() != null) {
            clearOldItems(shoppingList.getOrder().getId(), items);
        }
        else{
            shoppingList.setItems(items);
        }



        shoppingList.setNeedsUpdate(false);


        return shoppingListRepo.save(shoppingList);

    }

    public void save(ShoppingList list) {
        shoppingListRepo.save(list);
    }

    void clearOldItems(Long id, List<ShoppingListItem> newItems){
        ShoppingList shoppingList = getShoppingListByOrderId(id);

        if(shoppingList != null) {
            List<ShoppingListItem> old = shoppingList.getItems();
            newItems.forEach(
                    newItem->{
                        ShoppingListItem inOld = old.stream().filter(elem->elem.getInsideIngredientId() == newItem.getInsideIngredientId()).findFirst().orElse(null);
                        if(inOld != null) {
                            newItem.setBought(inOld.isBought());
                        }
                    }
            );
            old.clear();
            shoppingListItemRepo.saveAll(newItems);
            //old.addAll(newItems);
            //shoppingListRepo.save(shoppingList);
//            shoppingList.getItems().forEach(
//                    x->{
//                        ShoppingListItem item = items.stream().filter(y-> Objects.equals(y.getIngredient().getIngredient().getId(), x.getIngredient().getIngredient().getId())).findFirst().orElse(null);
//                        if(item != null) {
//                            item.setBought(x.isBought());
//                        }
//                    }
//            );
//
//            shoppingList.getItems().removeIf(x->
//                    items.stream().noneMatch(y->y.getIngredient().getIngredient().getId() == x.getIngredient().getIngredient().getId())
//            );
//
//            shoppingList.getItems().addAll(
//                    items.stream().filter(x->shoppingList.getItems().stream().noneMatch(it->it.getIngredient().getIngredient().getId()==x.getIngredient().getIngredient().getId())).toList()
//            );
//            //shoppingList.setItems(result);
//
//            shoppingList.getItems().forEach(el-> System.out.println(el.getIngredient().getIngredient().getName() + " " + el.isBought()));
//
//            shoppingListRepo.save(shoppingList);

        }
    }

    public ShoppingListItem getItemById(long id){
        return shoppingListItemRepo.findById(id).orElse(null);
    }

    public void saveItem(ShoppingListItem item){
        shoppingListItemRepo.save(item);
    }

    public long[] findSelectedItems(long shoppingId){
        ShoppingList shoppingList = shoppingListRepo.findById(shoppingId).orElse(null);
        if(shoppingList != null) {
            return shoppingList.getItems().stream().filter(ShoppingListItem::isBought).mapToLong(ShoppingListItem::getId).toArray();
        }
        return null;

    }

    public ShoppingListItem addCommentToItem(String comment, long itemId){
        ShoppingListItem item = getItemById(itemId);
        if(item != null) {
            item.setComment(comment);
            shoppingListItemRepo.save(item);
            return item;
        }
        return null;
    }

    public ShoppingListItem removeComment(long shoppingItemId) {
        ShoppingListItem item = getItemById(shoppingItemId);
        if(item != null) {
            item.setComment(null);
            shoppingListItemRepo.save(item);
            return item;
        }
        return null;
    }
}
