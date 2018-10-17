package test.pivotal.pal.tracker;

import io.pivotal.pal.tracker.CustomHealthIndicator;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CustomHealthIndicatorTest {
    TimeEntryRepository repository;
    CustomHealthIndicator subject;

    @Before
    public void setup() {
        repository = mock(TimeEntryRepository.class);
        subject = new CustomHealthIndicator(repository);
    }

    @Test
    public void health_bad() throws Exception {
        doReturn(asList(null, null, null, null))
                .when(repository)
                .list();

        Health health = subject.health();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void health_good() throws Exception {
        doReturn(asList(null, null, null, null, null))
                .when(repository)
                .list();

        Health health = subject.health();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }
}