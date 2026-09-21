package com.example.storemymeal.controller;

import com.example.storemymeal.model.MealAnalysisResponse;
import com.example.storemymeal.model.NutritionFact;
import com.example.storemymeal.service.NutritionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nutrition")
public class FactController {

    private final NutritionService nutritionService;

    public FactController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<MealAnalysisResponse> analyze(@RequestBody Map<String, String> request) {
        String meal = request.get("meal");
        if (meal == null || meal.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        MealAnalysisResponse response = nutritionService.processMeal(meal);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodaySummary() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<NutritionFact> todaysMeals = nutritionService.getMealsBetween(startOfDay, endOfDay);

        double totalCalories = todaysMeals.stream().mapToDouble(NutritionFact::getCalories).sum();
        double totalProtein = todaysMeals.stream().mapToDouble(NutritionFact::getProtein).sum();
        double totalFat = todaysMeals.stream().mapToDouble(NutritionFact::getFat).sum();
        double totalFiber = todaysMeals.stream().mapToDouble(NutritionFact::getFiber).sum();

        return ResponseEntity.ok(Map.of(
                "count", todaysMeals.size(),
                "calories", totalCalories,
                "protein", totalProtein,
                "fat", totalFat,
                "fiber", totalFiber
        ));
    }
}
