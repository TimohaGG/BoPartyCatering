package com.boparty.bopartycatering.Models.Position;

import com.boparty.bopartycatering.Models.Order.Orders;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class PositionAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Orders order;
    private int amount;

    public static PositionAmount copyPositionAmount(PositionAmount old, Orders order){
        PositionAmount positionAmount = new PositionAmount();
        positionAmount.position = old.position;
        positionAmount.order = order;
        positionAmount.amount = old.amount;
        return positionAmount;

    }

    public PositionAmount() {

    }

    public PositionAmount(Position position, Orders order, int amount) {
        this.position = position;
        this.order = order;
        this.amount = amount;
    }
    public PositionAmount(Position position, int amount) {
        this.position = position;
        this.amount = amount;
    }


    public void setPosition(Position position) {
        this.position = position;
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

    public String getPosName() {
        return position.getName();
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public void removeId(){
        id = null;
    }


}
