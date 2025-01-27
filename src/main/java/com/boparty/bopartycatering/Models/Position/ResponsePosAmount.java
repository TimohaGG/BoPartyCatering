package com.boparty.bopartycatering.Models.Position;

public class ResponsePosAmount {
    private int amount;
    private long id;
    private String name;

    public ResponsePosAmount(int amount, long id, String name) {
        this.amount = amount;
        this.id = id;
        this.name = name;
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
