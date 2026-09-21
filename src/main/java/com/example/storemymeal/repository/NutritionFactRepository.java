package com.example.storemymeal.repository;

import com.example.storemymeal.model.NutritionFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NutritionFactRepository extends JpaRepository<NutritionFact, Long>  {
    List<NutritionFact> findByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
