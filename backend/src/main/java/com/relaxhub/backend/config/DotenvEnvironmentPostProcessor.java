package com.relaxhub.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Loads backend/.env into Spring's environment before datasource configuration runs.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "relaxhubDotenv";

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment,
            SpringApplication application
    ) {
        if (isTestRuntime()) {
            return;
        }

        Optional<Path> envDirectory = findEnvDirectory();
        if (envDirectory.isEmpty()) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(envDirectory.get().toString())
                .filename(".env")
                .ignoreIfMissing()
                .load();

        Map<String, Object> properties = new LinkedHashMap<>();
        dotenv.entries().forEach(entry -> properties.put(entry.getKey(), entry.getValue()));

        if (!properties.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }

    private static Optional<Path> findEnvDirectory() {
        Path cwd = Path.of("").toAbsolutePath().normalize();

        Path directEnv = cwd.resolve(".env");
        if (Files.isRegularFile(directEnv)) {
            return Optional.of(cwd);
        }

        Path backendEnv = cwd.resolve("backend").resolve(".env");
        if (Files.isRegularFile(backendEnv)) {
            return Optional.of(cwd.resolve("backend"));
        }

        return Optional.empty();
    }

    private static boolean isTestRuntime() {
        return System.getProperty("surefire.test.class.path") != null
                || System.getProperty("surefire.real.class.path") != null;
    }
}
