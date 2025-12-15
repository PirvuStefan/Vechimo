package org.example.vechimo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvLoader {

    /**
     * Loads environment variables from a file located in the same directory as the
     * running JAR file, or the project root/build output in an IDE.
     *
     * @param fileName The name of the environment file (e.g., ".env").
     * @param loadFromSystemEnv If true, existing system environment variables are
     * also included, with file variables taking precedence.
     * @return A Map containing the loaded environment variables.
     */
    public static Map<String, String> loadEnvFromJarDirectory(String fileName, boolean loadFromSystemEnv) {
        // 1. Initialize the map with system environment variables if requested
        Map<String, String> env = loadFromSystemEnv ? new HashMap<>(System.getenv()) : new HashMap<>();

        File envFile = null;
        try {
            // Find the directory of the running code (JAR or IDE output folder)
            File baseDirectory = getBaseDirectory();

            if (baseDirectory != null) {
                envFile = new File(baseDirectory, fileName);

                if (envFile.exists() && !envFile.isDirectory()) {
                    System.out.println("Loading environment variables from: " + envFile.getAbsolutePath());
                    // 2. Read the .env file and populate the map
                    try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Simple parsing for key=value lines, ignoring comments (#)
                            line = line.trim();
                            if (line.isEmpty() || line.startsWith("#")) {
                                continue;
                            }
                            int equalsIndex = line.indexOf('=');
                            if (equalsIndex > 0) {
                                String key = line.substring(0, equalsIndex).trim();
                                String value = line.substring(equalsIndex + 1).trim();

                                // Simple quote removal for values (optional, but common for .env files)
                                if (value.startsWith("\"") && value.endsWith("\"")) {
                                    value = value.substring(1, value.length() - 1);
                                } else if (value.startsWith("'") && value.endsWith("'")) {
                                    value = value.substring(1, value.length() - 1);
                                }

                                // Add or overwrite existing variables
                                env.put(key, value);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading environment file: " + envFile.getAbsolutePath());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Environment file not found at: " + envFile.getAbsolutePath());
                }
            } else {
                System.err.println("Could not determine the base directory of the application.");
            }

        } catch (URISyntaxException e) {
            System.err.println("Error determining file path: " + e.getMessage());
            e.printStackTrace();
        }

        return env;
    }

    /**
     * A utility method to determine the base directory (JAR directory or IDE output directory).
     * @return The File object representing the base directory, or null if it cannot be determined.
     * @throws URISyntaxException If the URL cannot be converted to a URI.
     */
    private static File getBaseDirectory() throws URISyntaxException {
        // Use the class that calls this method (EnvLoader.class) to find the path.
        URL location = EnvLoader.class.getProtectionDomain().getCodeSource().getLocation();
        URI uri = location.toURI();
        File codeSource = new File(uri);

        if (codeSource.isDirectory()) {
            // Case 1: Running in an IDE (e.g., IntelliJ, Eclipse).
            // The location will point to a build output directory (e.g., /target/classes).
            // We can often assume the .env file is one or two levels up (in the project root).
            System.out.println("Running from directory: " + codeSource.getAbsolutePath());
            return codeSource.getParentFile().getParentFile(); // Adjust this depth as needed for your specific IDE setup.
        } else {
            // Case 2: Running from an executable JAR file.
            // The location points to the JAR file itself. We need its parent directory.
            System.out.println("Running from JAR file: " + codeSource.getAbsolutePath());
            return codeSource.getParentFile();
        }
    }
}