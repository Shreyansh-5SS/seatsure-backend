package com.seatsure.backend;

import com.seatsure.backend.entity.User;
import com.seatsure.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
	@Bean
    CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			if(userRepository.count() == 0) {
				System.out.println("--- NO USERS FOUND. CREATING FIRST SEATSURE USER ---");
				User firstUser = new User("shreyansh@seatsure.com", "super_secret_hash_123");
				userRepository.save(firstUser);
				System.out.println("--- USER SAVED SUCCESSFULLY TO POSTGRESQL! ---");
			}else{
				System.out.println("--- USERS ALREADY EXIST IN DB. SKIPPING CREATION ---");
			}
		};
	}
}