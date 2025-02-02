package com.boparty.bopartycatering.Models.User;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Models.Position.Ingredient;
import com.boparty.bopartycatering.Models.Position.Position;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter


public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public User(){
        id=0L;
        email = "";
        username = "";
        password = "";
        repeatPassword="";
    }

    private String email;

    private String username;
    private String password;
    @Transient
    private String repeatPassword;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Orders> orders;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Category> categories;

    public Long getId() {
        return id;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Ingredient> ingredients;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<Category> getCategories() {
        return categories;
    }



    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}

