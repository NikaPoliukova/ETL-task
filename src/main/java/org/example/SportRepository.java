package org.example;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportRepository extends ReactiveCrudRepository<Sport, Integer> {
}