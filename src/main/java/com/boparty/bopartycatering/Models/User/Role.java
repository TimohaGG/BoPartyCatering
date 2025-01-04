package com.boparty.bopartycatering.Models.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class Role implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Role(){

    }

    public Role(String role, Long id) {
        this.id = id;
        this.roleName = role;
    }
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }

}
