package com.boparty.bopartycatering.Models.Position;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
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
    private double weight;
    @Column(nullable = false)
    private double price;

    @Lob
    private byte[] image;

    @Transient
    private MultipartFile multipartFile;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "position")
    private List<IngredientAmount> ingredients;

    public Position() {

    }

    public Position(String name, Double weight, double price, MultipartFile multipartFile, Category category, List<IngredientAmount> ingredients) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.multipartFile = multipartFile;
        this.category = category;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    public int getPriceInt(){
        return (int)price;
    }

    public byte[] getImage() {

        return image;
    }

    public String getImageBase64() {
        if(image == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(image);
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



    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setIngredients(List<IngredientAmount> ingredients) {
        this.ingredients = ingredients;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
