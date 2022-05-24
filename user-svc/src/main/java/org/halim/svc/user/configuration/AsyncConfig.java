package org.halim.svc.user.configuration;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public Executor taskExecutor() {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(1000);
    executor.setThreadNamePrefix("Async-Thread-");

    // Copy sl4j Mapped Diagnostic Context (MDC) from the main to the child thread.
    executor.setTaskDecorator(
      runnable -> {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
          try {
            MDC.setContextMap(contextMap);
            runnable.run();
          } finally {
            MDC.clear();
          }
        };
      });
    executor.initialize();

    // Delegate spring security context to the async threads
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }
}
