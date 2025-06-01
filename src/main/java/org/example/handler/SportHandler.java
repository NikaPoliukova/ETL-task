package org.example.handler;

import lombok.RequiredArgsConstructor;
import org.example.entity.Sport;
import org.example.repository.SportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SportHandler {

    private final SportRepository sportRepository;

    public Mono<ServerResponse> createSport(ServerRequest request) {
        String sportName = request.pathVariable("sportname");
        Sport newSport = new Sport(sportName);

        return sportRepository.existsByName(sportName)
                .flatMap(exists -> {
                    if (exists) {
                        return ServerResponse
                                .status(HttpStatus.CONFLICT)
                                .bodyValue("Sport name already exists");
                    }
                    return sportRepository.save(newSport)
                            .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
                });
    }

    public Mono<ServerResponse> searchSports(ServerRequest request) {
        String query = request.queryParam("q").orElse("");
        return ServerResponse.ok().body(sportRepository.findByNameContainingIgnoreCase(query), Sport.class);
    }
}
