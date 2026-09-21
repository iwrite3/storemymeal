//package com.example.storemymeal.service;
//
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestRunner implements CommandLineRunner {
//
//    private final DeepSeekService deepSeekService;
//
//    // Spring automatically injects the DeepSeekService here
//    public TestRunner(DeepSeekService deepSeekService) {
//        this.deepSeekService = deepSeekService;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("\n=== TESTING DEEPSEEK API ===");
//        try {
//            JsonNode result = deepSeekService.analyzeFood("2 eggs and toast");
//            System.out.println("Success! DeepSeek Response: \n" + result.toPrettyString());
//        } catch (Exception e) {
//            System.err.println("Error calling DeepSeek: " + e.getMessage());
//        }
//        System.out.println("============================\n");
//    }
//}