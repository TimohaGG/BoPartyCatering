package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Repos.OrdersRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersService {
    private OrdersRepos ordersRepos;
    @Autowired
    public OrdersService(OrdersRepos ordersRepos) {
        this.ordersRepos = ordersRepos;
    }

    public List<Orders> getAllOrders() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //String user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String username = user.getUsername();
        return ordersRepos.findAll().stream().filter(x->x.getUser().getUsername().equals(username)).toList();
    }

    public void save(Orders orders) {
        ordersRepos.save(orders);
    }
}
