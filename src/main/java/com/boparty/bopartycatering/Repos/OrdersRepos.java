package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrdersRepos extends JpaRepository<Orders, Long> {
    Orders getOrderById(Long id);
    List<Orders> findAllByUserId(Long userId);
    List<Orders> findAllByUserIdAndTemporaryTrue(Long userId);
}
