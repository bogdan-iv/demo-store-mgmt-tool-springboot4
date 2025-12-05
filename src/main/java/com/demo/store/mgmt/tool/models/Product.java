package com.demo.store.mgmt.tool.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter // Use specific getters rather than @Data
@Setter // Use specific setters
@NoArgsConstructor // The essential no-arg constructor for JPA
@AllArgsConstructor // A handy constructor for creating full objects
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Version
    private Long version;
}