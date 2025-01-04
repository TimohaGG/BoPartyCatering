package com.boparty.bopartycatering.Models.Position;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;


}
