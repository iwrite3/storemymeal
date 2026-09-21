package com.example.storemymeal.service;

import com.example.storemymeal.model.MealAnalysisResponse;
import com.example.storemymeal.model.NutritionFact;
import com.example.storemymeal.repository.NutritionFactRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NutritionService {

    private final DeepSeekService deepSeekService;
    private final NutritionFactRepository repository;

    // Constructor injection
    public NutritionService(DeepSeekService deepSeekService, NutritionFactRepository repository) {
        this.deepSeekService = deepSeekService;
        this.repository = repository;
    }

    public MealAnalysisResponse processMeal(String foodDescription) {
        JsonNode aiData = deepSeekService.analyzeFood(foodDescription);

        // Map values to entity
        NutritionFact fact = new NutritionFact();
        fact.setFoodName(foodDescription);
        fact.setCalories(aiData.path("calories").asDouble(0.0));
        fact.setProtein(aiData.path("protein").asDouble(0.0));
        fact.setFat(aiData.path("fat").asDouble(0.0));
        fact.setFiber(aiData.path("fiber").asDouble(0.0));

        // Save only the nutrition fields and timestamp to PostgreSQL
        NutritionFact saved = repository.save(fact);

        // Combine DB entity details with the AI suggestion for the view
        return new MealAnalysisResponse(
                saved.getId(),
                saved.getFoodName(),
                saved.getCalories(),
                saved.getProtein(),
                saved.getFat(),
                saved.getFiber(),
                saved.getCreatedAt(),
                aiData.path("suggestion").asText()
        );
    }

    // <-- ADD THIS METHOD HERE -->
    public List<NutritionFact> getMealsBetween(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return repository.findByCreatedAtBetween(startOfDay, endOfDay);
    }
}