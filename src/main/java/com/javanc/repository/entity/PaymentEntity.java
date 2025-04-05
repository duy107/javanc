package com.javanc.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "type")
    String type;

    // order
    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY)
    List<OrderEntity> orders = new ArrayList<>();

    // user
    @ManyToOne
    @JoinColumn(name = "product_id")
    UserEntity user;
}
