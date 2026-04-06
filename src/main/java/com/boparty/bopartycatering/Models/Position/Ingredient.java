package com.boparty.bopartycatering.Models.Position;

import com.boparty.bopartycatering.Models.User.User;
import jakarta.persistence.*;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public IngredientCategory getIngCategory() {
        return ingCategory;
    }

    public void setIngCategory(IngredientCategory ingCategory) {
        this.ingCategory = ingCategory;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private IngredientCategory ingCategory;

    public Long getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
