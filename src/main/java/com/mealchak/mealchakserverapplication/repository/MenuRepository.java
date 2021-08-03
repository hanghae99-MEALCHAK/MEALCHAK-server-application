package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByCategory(String category);
    List<Menu> findAllByOrderByCountDesc();
    List<Menu> findAll();
}
