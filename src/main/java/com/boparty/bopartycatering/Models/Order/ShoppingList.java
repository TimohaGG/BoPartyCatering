package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    private Orders order;

    public ShoppingList() {
    }

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ShoppingListItem> items;

    private boolean needsUpdate = false;

    public boolean isNeedsUpdate() {
        return needsUpdate;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingListItem> items) {
        this.items = items;
    }

    public void addItem(ShoppingListItem item) {
        if(this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public void clearItems() {
        if(this.items != null) {
            this.items.clear();
        }

    }

    public int getId() {
        return id;
    }

    public void clearOldItems(List<ShoppingListItem> newIngs) {
        if(this.items != null) {


            List<ShoppingListItem> result = newIngs.stream().peek(
                    x->{
                        ShoppingListItem item = this.items.stream().filter(y-> Objects.equals(y.getIngredient().getIngredient().getId(), x.getIngredient().getIngredient().getId())).findFirst().orElse(null);
                        if(item != null) {
                            x.setBought(item.isBought());
                        }
                    }
            ).toList();
            this.items.clear();
            this.items.addAll(result);

        }

    }


}
