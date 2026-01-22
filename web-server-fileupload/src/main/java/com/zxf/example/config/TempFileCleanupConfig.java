package com.zxf.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Configuration for scheduled cleanup of temporary multipart upload files.
 *
 * <p>This addresses potential file residue issues where:
 * <ul>
 *   <li>Files larger than {@code spring.servlet.multipart.file-size-threshold} are written to disk</li>
 *   <li>Temporary files may not be cleaned up immediately by GC</li>
 *   <li>Abnormal JVM termination may leave files behind</li>
 * </ul>
 */
@Slf4j
@Configuration
@EnableScheduling
public class TempFileCleanupConfig {
    @Value("${spring.servlet.multipart.location}")
    private String multipartLocation;

    /**
     * Cleanup temp multipart files older than 1 hour.
     * Runs every hour starting at application startup.
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 60000)
    public void cleanupTempMultipartFiles() {
        cleanupDirectory(Paths.get(multipartLocation), "upload_", 1);
    }

    /**
     * Cleanup Spring's multipart temp files (StandardMultipartFile naming pattern).
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 120000)
    public void cleanupSpringMultipartFiles() {
        cleanupDirectory(Paths.get(multipartLocation), "spring-", 1);
    }

    private void cleanupDirectory(Path dirPath, String prefix, int hoursOld) {
        if (!Files.exists(dirPath)) {
            return;
        }

        Instant cutoff = Instant.now().minus(hoursOld, ChronoUnit.HOURS);

        try (Stream<Path> files = Files.list(dirPath)) {
            files.filter(p -> p.getFileName().toString().startsWith(prefix))
                    .filter(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toInstant().isBefore(cutoff);
                        } catch (IOException e) {
                            log.warn("Failed to get last modified time for: {}", p, e);
                            return false;
                        }
                    })
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                            log.debug("Deleted temp file: {}", p);
                        } catch (IOException e) {
                            log.warn("Failed to delete temp file: {}", p, e);
                        }
                    });
        } catch (IOException e) {
            log.warn("Failed to list temp directory: {}", dirPath, e);
        }
    }
}