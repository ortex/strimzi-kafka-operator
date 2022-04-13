/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.operator.common;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.vertx.micrometer.backends.BackendRegistries;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleFunction;

/**
 * Wraps creation of Micrometer metrics.
 */
public class MicrometerMetricsProvider implements MetricsProvider {
    private final MeterRegistry metrics;

    /**
     * Constructor of the Micrometer metrics provider
     */
    public MicrometerMetricsProvider() {
        this.metrics = BackendRegistries.getDefaultNow();
    }

    /**
     * Returns the Micrometer MeterRegistry with all metrics
     *
     * @return  MeterRegistry
     */
    @Override
    public MeterRegistry meterRegistry() {
        return metrics;
    }

    /**
     * Creates new Counter type metric
     *
     * @param name          Name of the metric
     * @param description   Description of the metric
     * @param tags          Tags used for the metric
     * @return              Counter metric
     */
    @Override
    public Counter counter(String name, String description, Tags tags) {
        return Counter.builder(name)
                .description(description)
                .tags(tags)
                .register(metrics);
    }

    /**
     * Creates new Timer type metric
     *
     * @param name          Name of the metric
     * @param description   Description of the metric
     * @param tags          Tags used for the metric
     * @return              Timer metric
     */
    @Override
    public Timer timer(String name, String description, Tags tags) {
        return Timer.builder(name)
                .description(description)
                .sla(Duration.ofMillis(1000), Duration.ofMillis(5000), Duration.ofMillis(10000), Duration.ofMillis(30000), Duration.ofMillis(60000), Duration.ofMillis(120000), Duration.ofMillis(300000))
                .tags(tags)
                .register(metrics);
    }

    /**
     * Creates new Gauge type metric
     *
     * @param name          Name of the metric
     * @param description   Description of the metric
     * @param tags          Tags used for the metric
     * @return              AtomicInteger which represents the Gauge metric
     */
    @Override
    public AtomicInteger gauge(String name, String description, Tags tags) {
        AtomicInteger gauge = new AtomicInteger(0);
        Gauge.builder(name, () -> gauge)
                .description(description)
                .tags(tags)
                .register(metrics);

        return gauge;
    }

    /**
     * Creates new Gauge type metric
     *
     * @param name          Name of the metric
     * @param description   Description of the metric
     * @param tags          Tags used for the metric
     * @param value         An object with some state which the gauge's instantaneous value is determined from
     * @param fn            A function that yields a double value for the gauge, based on the state of obj.
     * @return              AtomicInteger which represents the Gauge metric
     */
    @Override
    public <T> Gauge gauge(String name, String description, Tags tags, T value, ToDoubleFunction<T> fn) {
        return Gauge.builder(name, value, fn)
                .description(description)
                .tags(tags)
                .register(metrics);
    }
}
