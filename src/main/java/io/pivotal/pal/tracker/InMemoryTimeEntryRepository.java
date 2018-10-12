package io.pivotal.pal.tracker;

import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private Map<Long, TimeEntry> entries = new HashMap<>();
    private long lastId = 0L;

    public TimeEntry create(TimeEntry timeEntry) {
        lastId += 1;
        TimeEntry entry = new TimeEntry(lastId, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        entries.put(lastId, entry);
        return entry;
    }

    public TimeEntry find(long id) {
        return entries.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(entries.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry entry = entries.get(id);
        entry.setDate(timeEntry.getDate());
        entry.setHours(timeEntry.getHours());
        entry.setUserId(timeEntry.getUserId());
        entry.setProjectId(timeEntry.getProjectId());
        return entry;
    }

    public TimeEntry delete(long id) {
        return entries.remove(id);
    }
}
