package com.boparty.bopartycatering.Models.Position;

import com.boparty.bopartycatering.Models.Order.Orders;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class PositionAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Orders order;
    private int amount;

    public PositionAmount(Position position, int amount) {
        this.position = position;
        this.amount = amount;
    }

    public long getPositionId(){
        return position.getId();
    }

    public void addAmount(int amount){
        this.amount += amount;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }
    public int getAmount(){
        return amount;
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Orders getOrder() {
        return order;
    }

    public String getPositionName() {
        return position.getName();
    }


}
