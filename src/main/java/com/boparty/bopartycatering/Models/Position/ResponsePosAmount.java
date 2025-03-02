package com.boparty.bopartycatering.Models.Position;

public class ResponsePosAmount {
    private int amount;
    private long id;
    private String name;
    private int price;

    public ResponsePosAmount(int amount, long id, String name, int price) {
        this.amount = amount;
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
