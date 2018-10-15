package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry timeEntry);

    TimeEntry find(long id);

    TimeEntry update(long eq, TimeEntry any);

    List<TimeEntry> list();

    void delete(long id);
}
