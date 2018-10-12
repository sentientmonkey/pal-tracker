package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry timeEntryToCreate);
    TimeEntry delete(long l);
    TimeEntry find(long l);
    List<TimeEntry> list();
    TimeEntry update(long eq, TimeEntry any);
}
