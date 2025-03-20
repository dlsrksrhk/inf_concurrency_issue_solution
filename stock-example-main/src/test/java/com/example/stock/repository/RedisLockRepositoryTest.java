package com.example.stock.repository;

import com.example.stock.domain.Stock;
import com.example.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLockRepositoryTest {

    @Autowired
    private RedisLockRepository lockRepository;

    @Test
    public void decrease_test() {
        assertEquals(true, lockRepository.lock("1"));
        assertEquals(false, lockRepository.lock("1"));
    }

}