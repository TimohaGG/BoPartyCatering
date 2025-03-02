package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.Units;

public class AmountUnit {
    private Units unit;
    private double amount;
    public AmountUnit(Units unit, double amount) {
        this.unit = unit;
        this.amount = amount;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
