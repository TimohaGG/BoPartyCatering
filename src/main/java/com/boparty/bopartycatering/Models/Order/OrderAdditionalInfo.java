package com.boparty.bopartycatering.Models.Order;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Base64;

@Entity
public class OrderAdditionalInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Orders order;

    @ColumnDefault("false")
    private boolean isCommon;

    private String title;
    private String description;
    @Lob
    private byte[] image;

    private int price;

    public Long getId() {
        return id;
    }

    @Nullable
    public Orders getOrder() {
        return order;
    }

    public void setOrder(@Nullable Orders order) {
        this.order = order;
    }

    public boolean isCommon() {
        return isCommon;
    }

    public void setCommon(boolean common) {
        isCommon = common;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
