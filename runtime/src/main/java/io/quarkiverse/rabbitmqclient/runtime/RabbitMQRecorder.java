package io.quarkiverse.rabbitmqclient.runtime;

import java.util.Map;
import java.util.function.Supplier;

import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkiverse.rabbitmqclient.RabbitMQClients;
import io.quarkus.arc.Arc;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class RabbitMQRecorder {

    public void registerShutdownTask(ShutdownContext shutdownContext) {
        RabbitMQClients producer = Arc.container().instance(RabbitMQClients.class).get();
        shutdownContext.addShutdownTask(producer::destroy);
    }

    public Supplier<RabbitMQClient> rabbitMQClientSupplier(String name) {
        RabbitMQClients producer = Arc.container().instance(RabbitMQClients.class).get();
        return () -> producer.getRabbitMQClient(name);
    }

    public Supplier<RabbitMQClient> rabbitMQClientSupplierMicrometerMetrics(String name, Map<String, String> tags) {
        RabbitMQClients producer = Arc.container().instance(RabbitMQClients.class).get();
        return () -> producer.getRabbitMQClient(name, new QuarkusMicrometerMetricsCollector(tags));
    }

    public Supplier<RabbitMQClient> rabbitMQClientSupplierMPMetrics(String name, Map<String, String> tags) {
        RabbitMQClients producer = Arc.container().instance(RabbitMQClients.class).get();
        return () -> producer.getRabbitMQClient(name, new QuarkusMPMetricsCollector(tags));
    }
}
