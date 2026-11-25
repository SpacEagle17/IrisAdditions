package com.spaceagle17.irisadditions.core.log;

import java.util.LinkedHashMap;
import java.util.function.LongSupplier;

// Spam protection. The time window is extended for as long as duplicates keep coming in
final class LogMessageQueue {
    static final long SPAM_TIME_WINDOW_MS = 300;
    static final int MAX_REPEATS = 3;

    private final LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();
    private final int maxSize;
    private final LongSupplier clock;

    private static final class Entry {
        long timestamp;
        int occurrences = 1;

        Entry(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    LogMessageQueue(int maxSize) {
        this(maxSize, System::currentTimeMillis);
    }

    LogMessageQueue(int maxSize, LongSupplier clock) {
        this.maxSize = maxSize;
        this.clock = clock;
    }

    synchronized boolean shouldLog(String message) {
        long now = clock.getAsLong();
        Entry entry = entries.get(message);
        if (entry != null && now - entry.timestamp <= SPAM_TIME_WINDOW_MS) {
            entry.timestamp = now;
            return ++entry.occurrences <= MAX_REPEATS;
        }

        entries.remove(message);
        entries.values().removeIf(old -> now - old.timestamp > SPAM_TIME_WINDOW_MS);
        if (entries.size() >= maxSize) {
            entries.remove(entries.keySet().iterator().next());
        }
        entries.put(message, new Entry(now));
        return true;
    }
}
