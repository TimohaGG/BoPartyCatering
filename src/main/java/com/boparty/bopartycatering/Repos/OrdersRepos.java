package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepos extends JpaRepository<Orders, Long> {
}
