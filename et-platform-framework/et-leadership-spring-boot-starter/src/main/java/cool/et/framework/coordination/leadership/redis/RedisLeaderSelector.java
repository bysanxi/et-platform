package cool.et.framework.coordination.leadership.redis;

import cool.et.framework.coordination.leadership.common.LeaderSelectedCallback;
import cool.et.framework.coordination.leadership.common.LeaderSelector;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class RedisLeaderSelector implements LeaderSelector {
    private static final Logger log = LoggerFactory.getLogger(RedisLeaderSelector.class);

    private final RedissonClient client;
    private final RLock lock;
    private final String leaderId;
    private final long pollMillis;
    private final LeaderSelectedCallback selected;
    private final Runnable removed;
    private final CountDownLatch stopped = new CountDownLatch(1);
    private volatile CountDownLatch term;
    private Thread worker;
    private volatile Thread callback;

    public RedisLeaderSelector(RedissonClient client, String lockId, String leaderId, long ttlMillis,
                               LeaderSelectedCallback selected, Runnable removed) {
        this.client = client;
        this.lock = client.getLock(lockId);
        this.leaderId = leaderId;
        this.pollMillis = Math.max(1, ttlMillis / 3);
        this.selected = Objects.requireNonNull(selected);
        this.removed = Objects.requireNonNull(removed);
    }

    @Override
    public synchronized void start() {
        if (worker == null && stopped.getCount() != 0) {
            worker = Thread.ofPlatform().daemon().name("leader-election-" + leaderId).start(this::elect);
        }
    }

    @Override
    public void stop() {
        Thread electionThread;
        synchronized (this) {
            stopped.countDown();
            CountDownLatch active = term;
            if (active != null) {
                active.countDown();
            }
            electionThread = worker;
        }
        if (electionThread != null && Thread.currentThread() != electionThread && Thread.currentThread() != callback) {
            try {
                electionThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void elect() {
        try {
            while (stopped.getCount() != 0 && !client.isShuttingDown()) {
                try {
                    // No fixed lease: Redisson renews the lock while this client holds it.
                    if (lock.tryLock(pollMillis, TimeUnit.MILLISECONDS)) {
                        try {
                            lead();
                        } finally {
                            try {
                                lock.unlock();
                            } catch (IllegalMonitorStateException lost) {
                                log.debug("Redis leadership already expired for {}", leaderId);
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    log.warn("Redis election failed for {}", leaderId, e);
                }
                stopped.await(pollMillis, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void lead() throws InterruptedException {
        CountDownLatch released = new CountDownLatch(1);
        term = released;
        if (stopped.getCount() == 0) {
            term = null;
            return;
        }
        callback = Thread.ofVirtual().name("leader-selected-" + leaderId).unstarted(() -> {
            try {
                selected.run(released::countDown);
            } catch (Throwable e) {
                log.error("Leader selection callback failed for {}", leaderId, e);
                released.countDown();
            }
        });
        callback.start();
        try {
            while (!released.await(pollMillis, TimeUnit.MILLISECONDS)) {
                if (client.isShuttingDown() || !lock.isHeldByCurrentThread()) {
                    break;
                }
            }
        } finally {
            callback.interrupt();
            callback.join();
            callback = null;
            term = null;
            removed.run();
        }
    }
}
