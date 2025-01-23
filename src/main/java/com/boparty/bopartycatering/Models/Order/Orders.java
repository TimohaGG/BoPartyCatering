package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Models.User.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.Mapping;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Orders(){
        date = "";
        client = "";
        guestsAmount = 0;
        duration = 0;
        format = "";
        phone = "";
        id = 0L;
    }

    private String date;
    //private Date date;

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


    @OneToMany(mappedBy = "order")
    private List<PositionAmount> positionsAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderAdditionalInfo> additionalInfo;

    @ManyToOne
    private User user;

    public String getDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date res = formatter.parse(date);
            formatter = new SimpleDateFormat("dd.MM.yyyy");
            return formatter.format(res);
        }
        catch (Exception e){
            return "";
        }


    }

    public double getTotalPrice(){
        return positionsAmount.stream().mapToDouble(x -> x.getPosition().getPrice()).sum();
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

    public void setDate(String date) {
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
}
