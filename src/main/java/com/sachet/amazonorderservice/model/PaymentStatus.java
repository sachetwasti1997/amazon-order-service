package com.sachet.amazonorderservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentStatus {
    private String orderId;
    private String paymentId;
    private String paymentStatus;
}
