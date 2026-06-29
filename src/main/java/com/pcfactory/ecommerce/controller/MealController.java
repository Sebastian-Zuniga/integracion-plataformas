package com.pcfactory.ecommerce.controller;

import com.pcfactory.ecommerce.dto.Category;
import com.pcfactory.ecommerce.dto.MealDTO;
import com.pcfactory.ecommerce.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class MealController {

    @Autowired
    private MealService mealService;


    @GetMapping("/categories")
    public Mono<List<Category>> getCategories() {
        return mealService.getCategories();
    }


    @GetMapping("/search")
    public Mono<List<MealDTO>> searchMeals(@RequestParam String name) {
        return mealService.searchMealsByName(name);
    }


    @GetMapping("/lookup")
    public Mono<List<MealDTO>> getMealDetailsMono(@RequestParam String id) {
        return mealService.getMealById(id);
    }


    @GetMapping("/filter")
    public Mono<List<MealDTO>> getMealsByIngredient(@RequestParam String ingredient) {
        return mealService.filterByIngredient(ingredient);
    }
}