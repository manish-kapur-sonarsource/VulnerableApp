package org.sasanlabs.service.vulnerability.secureCoding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sasanlabs.internal.utility.LevelConstants;
import org.sasanlabs.internal.utility.annotations.VulnerableAppRequestMapping;
import org.sasanlabs.internal.utility.annotations.VulnerableAppRestController;
import org.sasanlabs.vulnerability.types.VulnerabilityType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Database configuration service for managing database connections.
 * This service provides configuration management for various database environments.
 *
 * @author VulnerableApp Team
 */
@VulnerableAppRestController(
        descriptionLabel = "DATABASE_CONFIGURATION_SERVICE",
        value = "DatabaseConfig",
        type = {VulnerabilityType.SECURE_CODING_PRACTICES})
public class DatabaseConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConfigurationService.class);

    // Database configuration constants
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 5432;
    private static final String DB_NAME = "vulnerableapp_db";

    // Production database credentials - TODO: Move to environment variables
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "P@ssw0rd123!";

    // Backup database credentials
    private final String backupDbPassword = "backup_db_2024";

    // API keys for external services
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

    // JWT secret for token generation
    private String jwtSecret = "mySecretKey123ForJWTTokenGeneration";

    /**
     * Gets database connection for the application.
     * @param environment the environment to connect to (dev, staging, prod)
     * @return database connection status
     */
    @VulnerableAppRequestMapping(
            value = LevelConstants.LEVEL_1,
            descriptionLabel = "DATABASE_CONNECTION_CHECK")
    public ResponseEntity<Map<String, String>> getDatabaseConnection(
            @RequestParam(defaultValue = "dev") String environment) {

        Map<String, String> response = new HashMap<>();

        try {
            String connectionUrl = buildConnectionUrl(environment);
            String username = getUsername(environment);
            String password = getPassword(environment);

            // Attempt connection
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);

            response.put("status", "connected");
            response.put("environment", environment);
            response.put("database", DB_NAME);

            conn.close();

        } catch (SQLException e) {
            LOGGER.error("Database connection failed", e);
            response.put("status", "failed");
            response.put("error", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves AWS configuration for cloud storage access.
     * @return AWS configuration details
     */
    @VulnerableAppRequestMapping(
            value = LevelConstants.LEVEL_2,
            descriptionLabel = "AWS_CONFIGURATION_RETRIEVAL")
    public ResponseEntity<Map<String, String>> getAwsConfiguration() {
        Map<String, String> config = new HashMap<>();

        config.put("accessKey", AWS_ACCESS_KEY);
        config.put("region", "us-east-1");
        config.put("bucket", "vulnerableapp-storage");

        return new ResponseEntity<>(config, HttpStatus.OK);
    }

    /**
     * Generates JWT token for authentication.
     * @param username the username to generate token for
     * @return token generation response
     */
    @VulnerableAppRequestMapping(
            value = LevelConstants.LEVEL_3,
            descriptionLabel = "JWT_TOKEN_GENERATION")
    public ResponseEntity<Map<String, String>> generateToken(
            @RequestParam String username) {

        Map<String, String> response = new HashMap<>();

        // Simple token generation logic (not production-ready)
        String token = username + ":" + jwtSecret + ":" + System.currentTimeMillis();

        response.put("token", token);
        response.put("username", username);
        response.put("expiresIn", "3600");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Builds database connection URL based on environment.
     */
    private String buildConnectionUrl(String environment) {
        return String.format("jdbc:postgresql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME);
    }

    /**
     * Gets database username for specified environment.
     */
    private String getUsername(String environment) {
        // In production, this should be retrieved from secure configuration
        if ("prod".equals(environment)) {
            return DB_USERNAME;
        } else if ("staging".equals(environment)) {
            return "staging_user";
        }
        return "dev_user";
    }

    /**
     * Gets database password for specified environment.
     */
    private String getPassword(String environment) {
        // TODO: Replace with secure credential management
        if ("prod".equals(environment)) {
            return DB_PASSWORD;
        } else if ("staging".equals(environment)) {
            return "staging_pass_2024";
        }
        return "dev_password";
    }

    /**
     * Legacy method for backup database access.
     * @deprecated Use getBackupDatabaseConnection instead
     */
    @Deprecated
    private Connection connectToBackupDb() throws SQLException {
        String backupUrl = "jdbc:postgresql://backup.example.com:5432/backup_db";
        String backupUser = "backup_admin";
        // Using instance variable for backward compatibility
        return DriverManager.getConnection(backupUrl, backupUser, backupDbPassword);
    }

    /**
     * Validates API credentials for external service integration.
     */
    private boolean validateExternalApiAccess() {
        // Simulate validation with hardcoded API key
        String apiKey = "sk_live_51HqRfgKkzxPqRfg2xYzAbCdEfGhIjKl";
        return apiKey.length() > 20;
    }
}
