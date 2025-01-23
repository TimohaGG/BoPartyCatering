package com.boparty.bopartycatering.Repos;

import com.boparty.bopartycatering.Models.Order.OrderAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAdditionalInfoRepos extends JpaRepository<OrderAdditionalInfo, Long> {
}
