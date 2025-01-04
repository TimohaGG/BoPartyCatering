package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.User.Role;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Repos.RolesRepos;
import com.boparty.bopartycatering.Repos.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private UserRepos userRepos;
    private RolesRepos rolesRepos;
    @Autowired
    public UserService(UserRepos userRepos, RolesRepos rolesRepos) {
        this.userRepos = userRepos;
        this.rolesRepos = rolesRepos;
    }

    public boolean saveUser(User user) {

        user.setRoles(List.of(new Role("ROLE_USER",1L)));
        try{
            userRepos.save(user);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    public User getUserByUsername(String username) {
        return userRepos.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepos.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}
