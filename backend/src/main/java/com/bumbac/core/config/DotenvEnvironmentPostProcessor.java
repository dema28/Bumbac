package com.bumbac.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.FileSystemResource;

import java.util.HashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String profile = environment.getProperty("spring.profiles.active", "local");
        String filename = ".env." + profile;

        System.out.println("🔍 Loading environment for profile: " + profile);

        FileSystemResource resource = new FileSystemResource(filename);
        if (resource.exists()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .filename(filename)
                        .load();

                Map<String, Object> envProperties = new HashMap<>();
                dotenv.entries().forEach(entry -> {
                    envProperties.put(entry.getKey(), entry.getValue());
                    System.out.println("   ✅ " + entry.getKey() + "=***");
                });

                environment.getPropertySources()
                        .addFirst(new MapPropertySource("dotenv", envProperties));

                System.out.printf("✅ Loaded %d variables from %s%n", envProperties.size(), filename);

            } catch (Exception e) {
                System.out.printf("❌ Error loading %s: %s%n", filename, e.getMessage());
            }
        } else {
            System.out.printf("ℹ️  %s not found, using system environment variables%n", filename);
        }
    }
}