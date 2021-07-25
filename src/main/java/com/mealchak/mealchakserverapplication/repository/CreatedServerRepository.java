package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.CreatedServers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreatedServerRepository extends JpaRepository<CreatedServers,Long> {
    List<CreatedServers> findByServerId(Long id);
    List<CreatedServers> findByServerCode(String ServerCode);
}
