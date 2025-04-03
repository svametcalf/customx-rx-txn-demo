package com.example.custom_rx_demo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SomeRecordRepository extends R2dbcRepository<CustomRxDemoApplication.SomeRecord, Long> {

    Flux<CustomRxDemoApplication.SomeRecord> findAllByOrderByCreatedAtDesc();
}
