package com.boparty.bopartycatering.Models.Position;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double weight;
    @Column(nullable = false)
    private double price;

    private byte[] image;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "position")
    private List<IngredientAmount> ingredients;

    public String getName() {
        return name;
    }

    public Double getWeight() {
        return weight;
    }

    public int getPrice() {
        return (int) price;
    }

    public byte[] getImage() {
        return image;
    }

    public Category getCategory() {
        return category;
    }

    public List<IngredientAmount> getIngredients() {
        return ingredients;
    }

    public Long getId() {
        return id;
    }
}
