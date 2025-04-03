package com.example.custom_rx_demo;

import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ReactiveTypeDescriptor;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@SpringBootApplication
public class CustomRxDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomRxDemoApplication.class, args);
	}

	@Controller
	public class CustomRxDemoController {

	}

	@Configuration
	@EnableR2dbcRepositories(basePackages = "com.example.custom_rx_demo")
	@EnableTransactionManagement
	@EnableR2dbcAuditing
	public class DatabaseConfig {

		@Bean
		public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
			return new R2dbcTransactionManager(connectionFactory);
		}

		@PostConstruct
		public void registerCustomRx() {
			ReactiveAdapterRegistry.getSharedInstance().registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(MonoWrapper.class),
					(wrapper) -> ((MonoWrapper<?>)wrapper).asMono(),
					(publisher) -> Mono.from(publisher).as(MonoWrapper::new)
					);
		}
	}

	@Table("some_records")
	public record SomeRecord(Integer id, String text, Instant createdAt) {}

	@Service
	public class SomeRecordService {
		private final SomeRecordRepository repository;

        public SomeRecordService(SomeRecordRepository repository) {
            this.repository = repository;
        }

		@Transactional
		public Mono<SomeRecord> save(SomeRecord someRecord) {
			return repository.save(someRecord);
		}

		public Flux<SomeRecord> getAll() {
			return repository.findAll();
		}

		@Transactional
		public MonoWrapper<SomeRecord> saveWrapped(SomeRecord someRecord) {
			return new MonoWrapper<>(repository.save(someRecord));
		}
	}
}
