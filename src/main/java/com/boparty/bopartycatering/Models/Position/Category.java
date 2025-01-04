package com.boparty.bopartycatering.Models.Position;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<Position> positions;

    public String getName() {
        return name;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public Long getId() {
        return id;
    }
}
