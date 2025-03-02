package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Order.ShoppingList;
import com.boparty.bopartycatering.Models.Order.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IShoppingListRepos extends JpaRepository<ShoppingList, Long> {
    ShoppingList findShoppingListByOrderId(Long order_id);


}
