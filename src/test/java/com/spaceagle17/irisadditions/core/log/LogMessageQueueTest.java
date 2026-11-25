package com.spaceagle17.irisadditions.core.log;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogMessageQueueTest {
    private final AtomicLong now = new AtomicLong(1_000);
    private final LogMessageQueue queue = new LogMessageQueue(2, now::get);

    @Test
    void suppressesAMessageRepeatedTooOften() {
        for (int i = 0; i < LogMessageQueue.MAX_REPEATS; i++) {
            assertTrue(queue.shouldLog("a"));
        }
        assertFalse(queue.shouldLog("a"));
    }

    @Test
    void logsAgainOnceTheTimeWindowPassed() {
        for (int i = 0; i <= LogMessageQueue.MAX_REPEATS; i++) {
            queue.shouldLog("a");
        }
        now.addAndGet(LogMessageQueue.SPAM_TIME_WINDOW_MS + 1);

        assertTrue(queue.shouldLog("a"));
    }

    @Test
    void forgetsTheOldestMessageWhenFull() {
        for (int i = 0; i <= LogMessageQueue.MAX_REPEATS; i++) {
            queue.shouldLog("a");
        }
        queue.shouldLog("b");
        queue.shouldLog("c");

        assertTrue(queue.shouldLog("a"));
    }
}
