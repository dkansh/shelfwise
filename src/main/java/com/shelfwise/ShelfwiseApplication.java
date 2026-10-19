package com.shelfwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableScheduling   // TODO: turn this on once OverdueLoanScheduler is tested
public class ShelfwiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShelfwiseApplication.class, args);
    }

}
