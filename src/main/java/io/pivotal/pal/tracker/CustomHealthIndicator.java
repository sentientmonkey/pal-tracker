package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private TimeEntryRepository repository;

    public CustomHealthIndicator(TimeEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        if (repository.list().size() >= 5) {
            return Health.down().build();
        }

        return Health.up().build();
    }
}
