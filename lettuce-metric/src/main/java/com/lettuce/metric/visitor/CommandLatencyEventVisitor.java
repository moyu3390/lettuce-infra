package com.lettuce.metric.visitor;

import com.codahale.metrics.MetricRegistry;
import io.lettuce.core.cluster.event.ClusterTopologyChangedEvent;
import io.lettuce.core.event.connection.ConnectedEvent;
import io.lettuce.core.event.connection.ConnectionActivatedEvent;
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent;
import io.lettuce.core.event.connection.DisconnectedEvent;
import io.lettuce.core.event.metrics.CommandLatencyEvent;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author heks
 * @description: TODO
 * @date 2020/4/20
 */
public class CommandLatencyEventVisitor implements EventVisitor {
    private final String name;
    private final MetricRegistry metrics;
    private final Map<String, String> metricNames;

    public CommandLatencyEventVisitor(final MetricRegistry metrics) {
        this(metrics, new HashMap<>());
    }

    public CommandLatencyEventVisitor(final String name, final MetricRegistry metrics) {
        this(name, metrics, new HashMap<>());
    }

    CommandLatencyEventVisitor(final MetricRegistry metrics, final Map<String, String> metricNames) {
        this.name = "event.latency";
        this.metrics = requireNonNull(metrics);
        this.metricNames = requireNonNull(metricNames);
    }

    CommandLatencyEventVisitor(final String name, final MetricRegistry metrics, final Map<String, String> metricNames) {
        this.name = MetricRegistry.name(requireNonNull(name), "event.latency");
        this.metrics = requireNonNull(metrics);
        this.metricNames = requireNonNull(metricNames);
    }

    @Override
    public void visit(final CommandLatencyEvent event) {
        event.getLatencies().forEach((commandLatencyId, commandMetrics) -> {
            final String commandType = commandLatencyId.commandType().name();
            final String metricName = metricNames.computeIfAbsent(commandType, (type) -> MetricRegistry.name(name, type));
            metrics.timer(metricName).update(commandMetrics.getCount(), commandMetrics.getTimeUnit());
        });
    }

    @Override
    public void visit(final DisconnectedEvent event) {
        // do nothing
    }

    @Override
    public void visit(final ClusterTopologyChangedEvent event) {
        // do nothing
    }

    @Override
    public void visit(final ConnectedEvent event) {
        // do nothing
    }

    @Override
    public void visit(final ConnectionActivatedEvent event) {
        // do nothing
    }

    @Override
    public void visit(final ConnectionDeactivatedEvent event) {
        // do nothing
    }
}
