package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class SportDataSetup {

    private final WebClient webClient;
    private final SportRepository sportRepository;

    @Value("${rakuten.api.app-id}")
    private String appId;

    public SportDataSetup(SportRepository sportRepository) {
        this.webClient = WebClient.create("https://app.rakuten.co.jp");
        this.sportRepository = sportRepository;
    }

    @PostConstruct
    public void fetchAndSaveSports() {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/api/IchibaItem/Search/20170706")
                        .queryParam("applicationId", appId)
                        .queryParam("keyword", "sport")
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(json -> {
                    JsonNode items = json.path("Items");
                    return Flux.fromIterable(items)
                            .map(item -> {
                                JsonNode itemNode = item.get("Item");
                                int id = itemNode.get("itemCode").asText().hashCode();
                                String name = itemNode.get("itemName").asText();
                                return new Sport(id, name);
                            });
                })
                .flatMap(sportRepository::save)
                .subscribe(
                        sport -> System.out.println("Saved sport: " + sport.getName()),
                        error -> System.err.println("Error: " + error.getMessage())
                );
    }
}
