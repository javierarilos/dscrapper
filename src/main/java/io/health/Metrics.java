package io.health;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public class Metrics {
    static final MetricRegistry metrics = new MetricRegistry();
    static final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
    private static boolean reporterStarted = false;

    public static synchronized void startReporter() {
        if (reporterStarted) {
            return;
        }
        reporter.start(1, TimeUnit.MINUTES);
        reporterStarted = true;
    }


    public static Timer timer(String timerName) {
        if(!reporterStarted) {
            startReporter();
        }
        return metrics.timer(name(Metrics.class, timerName));
    }
}
