package com.boparty.bopartycatering.Models.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfo {
    private  boolean tax = false;
    private  boolean needsForOne = false;
    private  String color = "";

    public boolean isTax() {
        return tax;
    }

    public void setTax(boolean tax) {
        this.tax = tax;
    }

    public boolean isNeedsForOne() {
        return needsForOne;
    }

    public void setNeedsForOne(boolean needsForOne) {
        this.needsForOne = needsForOne;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
