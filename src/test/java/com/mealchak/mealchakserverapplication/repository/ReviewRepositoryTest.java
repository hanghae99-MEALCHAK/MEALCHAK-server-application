package com.mealchak.mealchakserverapplication.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findAllByUserIdOrderByCreatedAtDesc() {
        // given

    }

    @Test
    void findByUserIdAndWriterId() {
    }

    @Test
    void deleteByIdAndWriter_Id() {
    }
}