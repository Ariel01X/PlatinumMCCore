package mc.platinum.com;

import java.time.Duration;

public class Cooldown {

    private final long nanosDuration;
    private long value;

    public Cooldown(Duration duration) {
        nanosDuration = duration.toNanos();
        value = System.nanoTime() - nanosDuration;
    }

    /**
     * If the cooldown is still active, returns the time left. Otherwise,
     * returns {@code Duration.ZERO} and starts the cooldown
     * 
     * @return the time left if the cooldown is still active, otherwise zero
     */
    public Duration triggerOrGet() {
        long currentTime = System.nanoTime();
        long elapsed = currentTime - value;
        long timeLeft = nanosDuration - elapsed;
        if (timeLeft <= 0) {
            value = currentTime;
            return Duration.ZERO;
        }
        return Duration.ofNanos(timeLeft);
    }

    @Override
    public String toString() {
        return "Cooldown [nanosDuration=" + nanosDuration + ", value=" + value + "]";
    }

}
