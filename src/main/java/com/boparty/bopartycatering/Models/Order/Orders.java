package com.boparty.bopartycatering.Models.Order;

import com.boparty.bopartycatering.Models.Position.PositionAmount;
import com.boparty.bopartycatering.Models.User.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
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
}
