package com.ealanta.bootiful;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.hint.TypeHints;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

@SpringBootApplication
@Log
@TypeHint(types = Customer.class)
@ResourceHint(patterns = {"git.properties"})
public class BootifulApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootifulApplication.class, args);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(DatabaseClient dbClient, CustomerRepository repo){
		return (event) -> {
			var ddl = dbClient.sql("create table customer(id serial primary key, name varchar(255) not null)").fetch().rowsUpdated();

			var names = Flux.just("Josh","Tanzu","David")
					.map(name -> new Customer(null, name))
					.flatMap(repo::save);

		  var all=  repo.findAll();

			ddl.thenMany(names).thenMany(all).subscribe(System.out::println);
		};
	}

}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

@RequiredArgsConstructor
@org.springframework.web.bind.annotation.RestController
class RestController {
		private final CustomerRepository repo;

		@RequestMapping("/customers")
		public Flux<Customer> get(){
			return repo.findAll();
		}

}

// http://localhost:8080/actuator/health/liveness UP
// curl -XPOST http://localhost:8080/down
// http://localhost:8080/actuator/health/liveness DOWN
// curl http://localhost:8080/actuator/health | jq '.status' DOWN too
@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
class AvailabilityRestController {
	private final ApplicationContext ctx;

	@PostMapping("/down")
	void down() {
		AvailabilityChangeEvent.publish(ctx, LivenessState.BROKEN);
	}
}