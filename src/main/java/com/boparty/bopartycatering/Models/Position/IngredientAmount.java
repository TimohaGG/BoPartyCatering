package com.boparty.bopartycatering.Models.Position;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity

public class IngredientAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    private Units unit;

    private double amount;

    public IngredientAmount() {}

    public IngredientAmount(Ingredient ingredient, double amount, Units unit) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }

    public IngredientAmount(Long id, Ingredient ingredient, Units unit, double amount, Position position) {
        this.id = id;
        this.ingredient = ingredient;
        this.unit = unit;
        this.amount = amount;
        this.position = position;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    public Long getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }

    public double getAmount() {
        return Math.round(amount * 100.0) / 100.0;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof IngredientAmount other) {
            return Objects.equals(this.getIngredient().getId(), other.getIngredient().getId())
                    && Objects.equals(this.getUnit().getId(), other.getUnit().getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIngredient().getId(), getUnit().getId());
    }


}
