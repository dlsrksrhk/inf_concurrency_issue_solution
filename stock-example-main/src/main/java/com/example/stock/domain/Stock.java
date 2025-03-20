package com.example.stock.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long quantity;

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

}
