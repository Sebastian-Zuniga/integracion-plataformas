package com.pcfactory.ecommerce.service;

import com.pcfactory.ecommerce.client.MealClient;
import com.pcfactory.ecommerce.dto.Category;
import com.pcfactory.ecommerce.dto.MealDTO;
import com.pcfactory.ecommerce.dto.CategoryResponse;
import com.pcfactory.ecommerce.dto.MealResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;

@Service
public class MealService {

    @Autowired
    private MealClient mealClient;

    // 1. Categorías
    public Mono<List<Category>> getCategories() {
        return mealClient.getCategories()
                .map(response -> response.getCategories() != null ? response.getCategories() : Collections.emptyList());
    }

    // 2. Buscar por nombre
    public Mono<List<MealDTO>> searchMealsByName(String name) {
        return mealClient.searchMealsByName(name)
                .map(response -> response.getMeals() != null ? response.getMeals() : Collections.emptyList());
    }

    // 3. Detalle por ID
    public Mono<List<MealDTO>> getMealById(String id) {
        return mealClient.getMealById(id)
                .map(response -> response.getMeals() != null ? response.getMeals() : Collections.emptyList());
    }

    // 4. Filtrar por ingrediente
    public Mono<List<MealDTO>> filterByIngredient(String ingredient) {
        return mealClient.filterByIngredient(ingredient)
                .map(response -> response.getMeals() != null ? response.getMeals() : Collections.emptyList());
    }
}