package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.example.entity.Sport;
import org.example.repository.SportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class SportDataSetup {

    private final WebClient webClient;
    private final SportRepository sportRepository;

    @Value("${rakuten.app-id}")
    private String appId;

    @Value("${rakuten.affiliate-id}")
    private String affiliateId;

    @Value("${rakuten.app-secret}")
    private String appSecret;

    @Value("${rakuten.callback-domain}")
    private String callbackDomain;

    @Value("${rakuten.api-host}")
    private String apiHost;

    @Value("${rakuten.api-path}")
    private String apiPath;

    public SportDataSetup(WebClient.Builder webClientBuilder, SportRepository sportRepository) {
        this.webClient = webClientBuilder.baseUrl("https://app.rakuten.co.jp/").build();
        this.sportRepository = sportRepository;
    }

    @PostConstruct
    public void fetchAndSaveSports() {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(apiHost)
                        .path(apiPath)
                        .queryParam("applicationId", appId)
                        .queryParam("keyword", "sport")
                        .queryParam("format", "json")
                        .queryParam("genreId", "555086")
                        .build())

                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(json -> {
                    JsonNode items = json.path("Items");
                    if (!items.isArray()) {
                        return Flux.error(new RuntimeException("Unexpected response format: 'Items' is not an array"));
                    }
                    return Flux.fromIterable(items)
                            .map(item -> {
                                JsonNode itemNode = item.get("Item");
                                int id = itemNode.get("itemCode").asText().hashCode();
                                String name = itemNode.get("itemName").asText();
                                return new Sport(id, name);
                            });
                })
                .flatMap(sportRepository::save)
                .doOnNext(sport -> System.out.println("Saved sport: " + sport.getName()))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .subscribe();
    }
}
