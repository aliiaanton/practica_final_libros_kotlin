package com.fempa.dam.users.api.config;

import com.fempa.dam.users.api.entity.user.UserEntity;
import com.fempa.dam.users.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

	private static final String DEFAULT_ADMIN_EMAIL = "admin@admin.com";
	private static final String DEFAULT_ADMIN_PASSWORD = "admin";

	@Bean
	CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.existsByEmail(DEFAULT_ADMIN_EMAIL)) {
				System.out.println("Admin user already exists with email: " + DEFAULT_ADMIN_EMAIL);
			} else {
				final UserEntity adminUser = UserEntity.builder().email(DEFAULT_ADMIN_EMAIL)
						.password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD)).name("Admin").role("ADMIN").build();
				userRepository.save(adminUser);
				System.out.println("Default admin user created with email: " + DEFAULT_ADMIN_EMAIL + " and password: "
						+ DEFAULT_ADMIN_PASSWORD);
			}
		};
	}

}
