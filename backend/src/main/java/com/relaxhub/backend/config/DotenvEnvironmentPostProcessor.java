package com.relaxhub.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;

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

        findEnvDirectory().ifPresent(envDirectory -> loadDotenv(environment, envDirectory));
        validateDatabaseUrl(environment);
    }

    private static void loadDotenv(ConfigurableEnvironment environment, Path envDirectory) {
        Dotenv dotenv = Dotenv.configure()
                .directory(envDirectory.toString())
                .filename(".env")
                .ignoreIfMissing()
                .load();

        Map<String, Object> properties = new LinkedHashMap<>();
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String existing = environment.getProperty(key);
            if (existing == null || existing.isBlank()) {
                properties.put(key, entry.getValue());
            }
        });

        if (!properties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }

    private static Optional<Path> findEnvDirectory() {
        Path start = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();

        for (Path dir = start; dir != null; dir = dir.getParent()) {
            if (Files.isRegularFile(dir.resolve(".env"))) {
                return Optional.of(dir);
            }

            Path backendEnvDir = dir.resolve("backend");
            if (Files.isRegularFile(backendEnvDir.resolve(".env"))) {
                return Optional.of(backendEnvDir);
            }
        }

        return Optional.empty();
    }

    private static void validateDatabaseUrl(ConfigurableEnvironment environment) {
        PropertySourcesPropertyResolver resolver =
                new PropertySourcesPropertyResolver(environment.getPropertySources());
        resolver.setIgnoreUnresolvableNestedPlaceholders(true);

        String dbUrl = resolver.getProperty("DB_URL");
        if (dbUrl != null && dbUrl.startsWith("jdbc:")) {
            return;
        }

        String datasourceUrl = resolver.getProperty("spring.datasource.url");
        if (datasourceUrl != null && datasourceUrl.startsWith("jdbc:")) {
            return;
        }

        throw new IllegalStateException(
                "Database URL is missing or invalid. Expected DB_URL to start with \"jdbc:\".\n"
                        + "- Copy backend/.env.example to backend/.env and set DB_URL, DB_USERNAME, DB_PASSWORD\n"
                        + "- In IntelliJ: Run → Edit Configurations → set Working directory to your backend folder\n"
                        + "  (e.g. .../relaxhub/backend)\n"
                        + "- Current user.dir: " + System.getProperty("user.dir")
        );
    }

    private static boolean isTestRuntime() {
        return System.getProperty("surefire.test.class.path") != null
                || System.getProperty("surefire.real.class.path") != null;
    }
}
