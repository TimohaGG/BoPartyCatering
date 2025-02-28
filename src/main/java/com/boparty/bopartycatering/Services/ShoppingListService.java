package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Order.ShoppingList;
import com.boparty.bopartycatering.Models.Order.ShoppingListItem;
import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import com.boparty.bopartycatering.Repos.IShoppingListItemRepos;
import com.boparty.bopartycatering.Repos.IShoppingListRepos;
import org.springframework.stereotype.Service;

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


        shoppingList.clearItems();
        for(IngredientAmount ing : ingredients) {
            shoppingList.addItem(new ShoppingListItem(ing, shoppingList));
        }

        shoppingList.setNeedsUpdate(false);


        return shoppingListRepo.save(shoppingList);

    }

    public void save(ShoppingList list) {
        shoppingListRepo.save(list);
    }
}
