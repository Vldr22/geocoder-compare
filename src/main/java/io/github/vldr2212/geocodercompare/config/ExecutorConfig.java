package io.github.vldr2212.geocodercompare.config;

import io.github.vldr2212.geocodercompare.properties.GeocoderExecutorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Пул потоков для параллельных вызовов геокодеров.
 */
@Configuration
@EnableConfigurationProperties(GeocoderExecutorProperties.class)
public class ExecutorConfig {

    public static final String GEOCODER_EXECUTOR = "geocoderExecutor";

    private static final String THREAD_NAME_PREFIX = "geocoder-";

    @Bean(GEOCODER_EXECUTOR)
    public Executor geocoderExecutor(GeocoderExecutorProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.corePoolSize());
        executor.setMaxPoolSize(properties.maxPoolSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(properties.awaitTerminationSeconds());
        executor.initialize();
        return executor;
    }
}