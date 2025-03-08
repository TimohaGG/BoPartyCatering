package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Models.User.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Orders(){
        date = LocalDate.now().atStartOfDay();
        client = "";
        guestsAmount = 0;
        duration = 0;
        format = "Бокси";
        phone = "0688714410";
        id = 0L;
        positionsAmount = new ArrayList<>();
        status = Status.CALCULATED;
    }

    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;


    @Column(nullable = true)
    private String client;
    @Column(nullable = true)
    private int guestsAmount;
    @Column(nullable = true)
    private int duration;
    @Column(nullable = true)
    private String format;
    @ColumnDefault("0688714410")
    private String phone;


    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PositionAmount> positionsAmount;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
    private List<OrderAdditionalInfo> additionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShoppingList shoppingList;
    @ColumnDefault("false")
    private boolean temporary;

    @ColumnDefault("CALCULATED")
    private Status status;

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if(date!=null){
            return date.format(formatter);
        }
        else{
            return "";
        }

    }

    public int getTotalPrice(){
        return positionsAmount.stream().mapToInt(x -> (int)x.getPosition().getPrice() * x.getAmount()).sum();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getPrice(){

        return positionsAmount.stream()
                .mapToInt(x -> (int)x.getPosition().getPrice() * x.getAmount())
                .sum();
    }

    public String getClient() {
        return client;
    }

    public int getGuestsAmount() {
        return guestsAmount;
    }

    public int getDuration() {
        return duration;
    }

    public String getFormat() {
        return format;
    }

    public String getPhone() {
        return phone;
    }

    public List<PositionAmount> getPositionsAmount() {
        return positionsAmount;
    }

    public User getUser() {
        return user;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setGuestsAmount(int guestsAmount) {
        this.guestsAmount = guestsAmount;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPositionsAmount(List<PositionAmount> positionsAmount) {
        this.positionsAmount = positionsAmount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderAdditionalInfo> getAdditionalInfo() {
        return additionalInfo;
    }

    public void addPosition(PositionAmount position) {
        positionsAmount.add(position);
    }

    public int getOnOnePerson(){
        return (int)getTotalPrice() / guestsAmount;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
