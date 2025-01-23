package com.boparty.bopartycatering.Models.Order;

import org.springframework.web.multipart.MultipartFile;

public class InfoDTO {


    private String title = "";
    private String description = "";
    private MultipartFile image;
    private boolean save = false;
    private int price = 0;

    public InfoDTO() {
        title = "";
        description = "";
        image = null;
        price = 0;
        save = false;
    }

    public boolean getSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
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
