package com.sachet.amazonorderservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    private String id;
    private String sellerEmail;
    private String sellerContact;
    private String userEmail;
    private String userContact;
    private String itemImage;
    private String itemTitle;
    private String status;
    private Date expiresAt;
    private String itemId;
    private Integer quantity;
    private Double itemPrice;
}
