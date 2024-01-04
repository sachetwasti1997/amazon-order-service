package com.sachet.amazonorderservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreatedEventModal {
    private String orderId;
    private String status;
    private String userId;
    private String expiresAt;
    private String itemId;
    private Double itemPrice;
}
