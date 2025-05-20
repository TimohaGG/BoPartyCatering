package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.IngredientAmount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private IngredientAmount ingredient;

    @ColumnDefault("false")
    private boolean isBought;

    public Long getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ShoppingList shoppingList;

    @Column(nullable = true)
    private String comment;


    public ShoppingListItem(IngredientAmount ingredient, ShoppingList shoppingList) {
        this.ingredient = ingredient;
        this.shoppingList = shoppingList;
    }

    public ShoppingListItem() {

    }

    public long getInsideIngredientId(){
        return ingredient.getIngredient().getId();
    }
    public IngredientAmount getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientAmount ingredient) {
        this.ingredient = ingredient;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
