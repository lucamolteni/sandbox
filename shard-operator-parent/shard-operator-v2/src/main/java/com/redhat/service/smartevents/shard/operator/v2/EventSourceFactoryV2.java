package com.redhat.service.smartevents.shard.operator.v2;

import java.util.Map;

import com.redhat.service.smartevents.shard.operator.core.utils.LabelsBuilder;
import com.redhat.service.smartevents.shard.operator.v2.resources.CamelIntegration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers;

public class EventSourceFactoryV2 {

    public static EventSource buildIntegrationInformer(KubernetesClient kubernetesClient, String componentName) {
        SharedIndexInformer<CamelIntegration> integrationInformer =
                kubernetesClient
                        .resources(CamelIntegration.class)
                        .inAnyNamespace()
                        .withLabels(buildLabels(componentName))
                        .runnableInformer(0);

        return new InformerEventSource<>(integrationInformer, Mappers.fromOwnerReference());
    }

    private static Map<String, String> buildLabels(String componentName) {
        return new LabelsBuilder()
                .withManagedByOperator()
                .withComponent(componentName)
                .build();
    }
}
