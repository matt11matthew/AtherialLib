package me.matthewedevelopment.atheriallib.uuid;

import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataColumnType;
import me.matthewedevelopment.atheriallib.database.registry.DataObject;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UUIDProfile extends DataObject<UUIDProfile> {
    private String username;
    private volatile Object profile;

    /**
     * Retry defaults (ticks). Kept conservative to avoid hammering providers.
     */
    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final long DEFAULT_INITIAL_DELAY_TICKS = 20L; // 1s
    private static final long DEFAULT_MAX_DELAY_TICKS = 20L * 30L; // 30s

    private final AtomicBoolean loading = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicInteger attempt = new AtomicInteger(0);

    public UUIDProfile(UUID uuid, String username) {
        super(uuid);
        this.username = username;
    }

    public UUIDProfile(UUID uuid) {
        super(uuid);
    }

    public UUIDProfile() {
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getTableName() {
        return "uuid_cache";
    }

    @Override
    public List<DataColumn> getDefaultColumns() {
        return Collections.singletonList(new DataColumn("username", DataColumnType.VARCHAR, username));
    }

    @Override
    public UUIDProfile loadResultFromSet(ResultSet resultSet) {
        try {
            this.username = resultSet.getString("username");
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProfileCasted(Class<T> clazz) {
        return (T) profile;
    }

    /**
     * Cancels any in-flight retry chain. After cancelling, calling {@link #load()} will start a fresh chain.
     */
    public void cancelLoad() {
        cancelled.set(true);
        loading.set(false);
        attempt.set(0);
    }

    /**
     * Loads the profile via the configured {@link ProfileProvider}.
     *
     * Adds non-blocking retry logic with exponential backoff + jitter.
     */
    public void load() {
        // Reset cancellation if the caller wants to start again.
        cancelled.set(false);

        UUIDProfileRegistry uuidProfileRegistry = UUIDProfileRegistry.get();
        if (uuidProfileRegistry == null) return;

        ProfileProvider<?> profileProvider = uuidProfileRegistry.getProfileProvider();
        if (profileProvider == null) return;

        // Avoid starting multiple concurrent load chains.
        if (!loading.compareAndSet(false, true)) return;

        // Start (or restart) attempts.
        attempt.set(0);
        doAttempt(profileProvider);
    }

    private void doAttempt(ProfileProvider<?> profileProvider) {
        if (cancelled.get()) {
            loading.set(false);
            return;
        }

        final int currentAttempt = attempt.incrementAndGet();

        profileProvider.handleLoading(uuid, username, o -> {
            // Success: store and stop retrying.
            profile = o;
            loading.set(false);
        }, throwable -> {
            if (cancelled.get()) {
                loading.set(false);
                return;
            }

            if (currentAttempt >= DEFAULT_MAX_ATTEMPTS) {
                loading.set(false);
                // Preserve previous behavior: print stacktrace so failures don't silently disappear.
                throwable.printStackTrace();
                return;
            }

            long delay = computeDelayTicks(currentAttempt);
            AtherialTasks.runIn(() -> doAttempt(profileProvider), delay);
        });
    }

    private static long computeDelayTicks(int attemptNumber) {
        // Exponential backoff: initial * 2^(attempt-1), capped.
        long base;
        try {
            base = Math.multiplyExact(DEFAULT_INITIAL_DELAY_TICKS, 1L << Math.max(0, attemptNumber - 1));
        } catch (ArithmeticException ex) {
            base = DEFAULT_MAX_DELAY_TICKS;
        }
        base = Math.min(base, DEFAULT_MAX_DELAY_TICKS);

        // Add 0-25% jitter to reduce thundering herds.
        long jitter = (long) (base * ThreadLocalRandom.current().nextDouble(0.0, 0.25));
        return Math.max(1L, base + jitter);
    }
}
