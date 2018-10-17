package test.pivotal.pal.tracker;

import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryController;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TimeEntryControllerTest {
    private TimeEntryRepository timeEntryRepository;
    private TimeEntryController controller;
    private GaugeService gaugeService;
    private CounterService counterService;
    private TimeEntry timeEntryToCreate;
    private TimeEntry savedTimeEntry;

    @Before
    public void setUp() throws Exception {
        timeEntryRepository = mock(TimeEntryRepository.class);
        gaugeService = mock(GaugeService.class);
        counterService = mock(CounterService.class);
        controller = new TimeEntryController(timeEntryRepository, counterService, gaugeService);

        timeEntryToCreate = new TimeEntry(123L, 456L, LocalDate.parse("2017-01-08"), 8);
        savedTimeEntry = timeEntryToCreate.withId(1);
    }

    @Test
    public void testCreate() throws Exception {
        doReturn(savedTimeEntry)
            .when(timeEntryRepository)
            .create(any(TimeEntry.class));

        ResponseEntity response = controller.create(timeEntryToCreate);

        verify(timeEntryRepository).create(timeEntryToCreate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(savedTimeEntry);
    }

    @Test
    public void testCreate_Counter() {
        doReturn(savedTimeEntry)
                .when(timeEntryRepository)
                .create(any(TimeEntry.class));

        controller.create(timeEntryToCreate);

        verify(counterService).increment("TimeEntry.created");
    }

    @Test
    public void testCreate_Gauge() {
        doReturn(savedTimeEntry)
                .when(timeEntryRepository)
                .create(any(TimeEntry.class));

        doReturn(Collections.singletonList(savedTimeEntry))
                .when(timeEntryRepository)
                .list();

        controller.create(timeEntryToCreate);

        verify(gaugeService).submit("timeEntries.count", 1);
    }

    @Test
    public void testRead() throws Exception {
        doReturn(savedTimeEntry)
            .when(timeEntryRepository)
            .find(1L);

        ResponseEntity<TimeEntry> response = controller.read(1L);

        verify(timeEntryRepository).find(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(savedTimeEntry);
    }

    @Test
    public void testRead_NotFound() throws Exception {
        doReturn(null)
            .when(timeEntryRepository)
            .find(1L);

        ResponseEntity<TimeEntry> response = controller.read(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testRead_CounterWhenFound() throws Exception {
        doReturn(savedTimeEntry)
                .when(timeEntryRepository)
                .find(1L);

        controller.read(1L);
        verify(counterService).increment("TimeEntry.read");
    }

    @Test
    public void testRead_CounterWhenNotFound() throws Exception {
        doReturn(null)
                .when(timeEntryRepository)
                .find(1L);

        controller.read(1L);
        verifyZeroInteractions(counterService);
    }

    @Test
    public void testList() throws Exception {
        List<TimeEntry> expected = asList(
                savedTimeEntry,
                new TimeEntry(2L, 789L, 321L, LocalDate.parse("2017-01-07"), 4)
        );
        doReturn(expected).when(timeEntryRepository).list();

        ResponseEntity<List<TimeEntry>> response = controller.list();

        verify(timeEntryRepository).list();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testList_Counter() throws Exception {
        doReturn(Collections.singletonList(savedTimeEntry)).when(timeEntryRepository).list();

        controller.list();

        verify(counterService).increment("TimeEntry.list");
    }

    @Test
    public void testUpdate() throws Exception {
        TimeEntry expected = new TimeEntry(1L, 987L, 654L, LocalDate.parse("2017-01-07"), 4);
        doReturn(expected)
            .when(timeEntryRepository)
            .update(eq(1L), any(TimeEntry.class));

        ResponseEntity response = controller.update(1L, expected);

        verify(timeEntryRepository).update(1L, expected);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate_NotFound() throws Exception {
        doReturn(null)
            .when(timeEntryRepository)
            .update(eq(1L), any(TimeEntry.class));

        ResponseEntity response = controller.update(1L, new TimeEntry());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdate_Counter() throws Exception {
        doReturn(savedTimeEntry)
                .when(timeEntryRepository)
                .update(eq(1L), any(TimeEntry.class));

        controller.update(1L, timeEntryToCreate);
        verify(counterService).increment("TimeEntry.updated");
    }

    @Test
    public void testUpdate_CounterNoUpdated() throws Exception {
        doReturn(null)
                .when(timeEntryRepository)
                .update(eq(1L), any(TimeEntry.class));

        controller.update(1L, timeEntryToCreate);
        verifyZeroInteractions(counterService);
    }

    @Test
    public void testDelete() throws Exception {
        ResponseEntity<TimeEntry> response = controller.delete(1L);
        verify(timeEntryRepository).delete(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void testDelete_Counter() throws Exception {
        controller.delete(1L);

        verify(counterService).increment("TimeEntry.deleted");
    }

    @Test
    public void testDelete_Gauge() throws Exception {
        doReturn(Collections.emptyList())
                .when(timeEntryRepository)
                .list();

        controller.delete(1L);

        verify(gaugeService).submit("timeEntries.count", 0);
    }
}
