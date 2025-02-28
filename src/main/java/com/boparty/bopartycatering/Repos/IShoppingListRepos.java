package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Order.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IShoppingListRepos extends JpaRepository<ShoppingList, Integer> {
    ShoppingList findShoppingListByOrderId(Long order_id);
}
