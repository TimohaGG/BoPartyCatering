package com.boparty.bopartycatering.Models.Order;

import jakarta.annotation.Nullable;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Builder
public class OrderInfoEditDto {
    private Long id;
    private Long orderId;
    private String title;
    private String description;
    private int price;
}
