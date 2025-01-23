package com.boparty.bopartycatering.Models.Order;

import org.springframework.web.multipart.MultipartFile;

public class InfoDTO {
    public boolean isCommon = false;

    public String title = "";
    public String description = "";
    public MultipartFile image;

    public int price = 0;

    public InfoDTO() {
        title = "";
        description = "";
        image = null;
        price = 0;
        isCommon = false;
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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
