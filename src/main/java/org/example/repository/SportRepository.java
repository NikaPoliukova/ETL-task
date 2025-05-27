package org.example.repository;

import org.example.entity.Sport;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SportRepository extends ReactiveCrudRepository<Sport, Integer> {
    Mono<Boolean> existsByName(String name);
    Mono<Sport> findByName(String name);
    Flux<Sport> findByNameContainingIgnoreCase(String query);
}