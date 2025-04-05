package com.javanc.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name="product_shoppingcart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductShoppingCartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "quantity")
    Long quantity;

    // product
    @ManyToOne
    @JoinColumn(name = "product_id")
    ProductEntity product;

    // shoppingcart
    @ManyToOne
    @JoinColumn(name = "shoppingcart_id")
    ShoppingCartEntity shoppingCart;
}
