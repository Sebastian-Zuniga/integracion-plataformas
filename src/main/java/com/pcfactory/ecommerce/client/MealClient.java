package com.pcfactory.ecommerce.client;

import com.pcfactory.ecommerce.dto.CategoryResponse;
import com.pcfactory.ecommerce.dto.MealResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MealClient {

    @Autowired
    private WebClient webClient;

    public Mono<CategoryResponse> getCategories(){
        return this.webClient.get()
                .uri("/categories.php")
                .retrieve()
                .bodyToMono(CategoryResponse.class);
    }

    public Mono<MealResponse> searchMealsByName(String name) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.php")
                        .queryParam("s", name)
                        .build())
                .retrieve()
                .bodyToMono(MealResponse.class);
    }

    public Mono<MealResponse> getMealById(String id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/lookup.php")
                        .queryParam("i", id)
                        .build())
                .retrieve()
                .bodyToMono(MealResponse.class);
    }

    public Mono<MealResponse> filterByIngredient(String ingredient) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/filter.php")
                        .queryParam("i", ingredient)
                        .build())
                .retrieve()
                .bodyToMono(MealResponse.class);
    }




}
