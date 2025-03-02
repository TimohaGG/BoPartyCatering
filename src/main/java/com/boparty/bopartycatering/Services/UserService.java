package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Position.Category;
import com.boparty.bopartycatering.Models.Position.Position;
import com.boparty.bopartycatering.Models.User.Role;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Repos.RolesRepos;
import com.boparty.bopartycatering.Repos.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepos userRepos;
    private RolesRepos rolesRepos;
    @Autowired
    public UserService(UserRepos userRepos, RolesRepos rolesRepos) {
        this.userRepos = userRepos;
        this.rolesRepos = rolesRepos;
    }
    public User getCurrentUser() {
        User usr = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepos.findByUsername(usr.getUsername());
    }

    public boolean saveUser(User user) {

        user.setRoles(rolesRepos.findAll().stream().filter(x->x.getRoleName().equals("ROLE_USER")).toList());
        user.setUsername(user.getEmail());
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
    public List<Category> getCategories(){
        return getCurrentUser().getCategories();
    }

    public List<Position> getPositions(){
        //User cur = getCurrentUser();
        List<Category> categories = getCategories();
        List<Position> pos = getCurrentUser().getCategories()
                .stream()
                .flatMap(x->
                        x.getPositions().stream())
                .collect(Collectors.toList());
        return pos;
    }


    public Long getFirstCategory() {
        return getCurrentUser().getCategories().get(0).getId();
    }
}
