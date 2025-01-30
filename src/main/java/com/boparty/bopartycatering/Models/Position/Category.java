package com.boparty.bopartycatering.Models.Position;

import com.boparty.bopartycatering.Models.User.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Position> positions;

    @ManyToOne
    private User user;

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
