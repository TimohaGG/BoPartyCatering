package com.boparty.bopartycatering.Models.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfo {
    private  boolean tax = false;
    private  boolean needsForOne = false;
    private  String color = "";
}
