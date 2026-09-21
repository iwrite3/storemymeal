package com.example.storemymeal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealAnalysisResponse {
    private Long id;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double fiber;
    private LocalDateTime createdAt;
    private String aiSuggestion; // Sent to client only
}