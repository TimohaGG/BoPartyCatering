package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Order.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IShoppingListItemRepos extends JpaRepository<ShoppingListItem, Long> {
//    ShoppingListItem findById(long id);
}
