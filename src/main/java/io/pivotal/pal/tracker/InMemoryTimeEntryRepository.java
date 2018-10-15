package io.pivotal.pal.tracker;

import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private Map<Long,TimeEntry> data;
    private long sequence;

    public InMemoryTimeEntryRepository() {
        data = new HashMap<>();
        sequence = 0L;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        sequence++;
        val newTimeEntry = timeEntry.withId(sequence);
        data.put(sequence, newTimeEntry);
        return newTimeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return data.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry entry) {
        return data.computeIfPresent(id, (_id, _entry) -> entry.withId(id));
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(long id) {
        data.remove(id);
    }
}
