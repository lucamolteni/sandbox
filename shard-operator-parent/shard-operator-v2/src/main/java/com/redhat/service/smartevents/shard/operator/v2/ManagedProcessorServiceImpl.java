package com.redhat.service.smartevents.shard.operator.v2;

import java.util.Arrays;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.service.smartevents.infra.v2.api.models.dto.ProcessorDTO;
import com.redhat.service.smartevents.shard.operator.core.providers.TemplateImportConfig;
import com.redhat.service.smartevents.shard.operator.v2.providers.TemplateProvider;
import com.redhat.service.smartevents.shard.operator.v2.resources.CamelIntegration;
import com.redhat.service.smartevents.shard.operator.v2.resources.KafkaConfigurationSpec;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedBridge;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedProcessor;

import io.fabric8.knative.eventing.contrib.kafka.v1beta1.KafkaSource;
import io.fabric8.kubernetes.client.KubernetesClient;

@ApplicationScoped
public class ManagedProcessorServiceImpl implements ManagedProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedProcessorServiceImpl.class);

    @Inject
    TemplateProvider templateProvider;

    @Inject
    KubernetesClient kubernetesClient;

    @Override
    public void createManagedProcessor(ProcessorDTO processorDTO, String namespace) {

        ManagedProcessor expected = ManagedProcessor.fromDTO(processorDTO, namespace);

        ManagedProcessor existing = kubernetesClient
                .resources(ManagedProcessor.class)
                .inNamespace(namespace)
                .withName(ManagedProcessor.resolveResourceName(processorDTO.getId()))
                .get();

        if (existing == null || !expected.getSpec().equals(existing.getSpec())) {
            ManagedProcessor managedProcessor = kubernetesClient
                    .resources(ManagedProcessor.class)
                    .inNamespace(namespace)
                    .createOrReplace(expected);
        } else {
            // notify manager of status change
        }
    }

    @Override
    public CamelIntegration fetchOrCreateCamelIntegration(ManagedProcessor processor, String integrationName) {
        TemplateImportConfig config = new TemplateImportConfig();
        CamelIntegration expected = templateProvider.loadCamelIntegrationTemplate(processor, config);
        expected.getMetadata().setName(integrationName);

        expected.getSpec().setFlows(Arrays.asList(processor.getSpec().getFlows()));

        String namespace = processor.getMetadata().getNamespace();
        CamelIntegration integration = kubernetesClient
                .resources(CamelIntegration.class)
                .inNamespace(namespace)
                .withName(integrationName)
                .get();

        if (integration == null) {
            return kubernetesClient
                    .resources(CamelIntegration.class)
                    .inNamespace(namespace)
                    .create(expected);
        }

        return integration;
    }

    @Override
    public KafkaSource fetchOrCreateKafkaSource(ManagedProcessor processor, ManagedBridge bridge) {
        String processorName = processor.getMetadata().getName();
        String namespace = processor.getMetadata().getNamespace();

        TemplateImportConfig config = new TemplateImportConfig()
                .withNameFromParent();

        KafkaSource expectedKafka = templateProvider.loadKafkaSourceTemplate(processor, config);

        KafkaConfigurationSpec kafkaConfiguration = bridge.getSpec().getkNativeBrokerConfiguration().getKafkaConfiguration();
        expectedKafka.getSpec().setBootstrapServers(Collections.singletonList(kafkaConfiguration.getBootstrapServers()));
        expectedKafka.getSpec().setConsumerGroup(String.format("%s-consumerGroup", processorName));
        expectedKafka.getSpec().setTopics(Collections.singletonList(String.format("%s-sourceTopic", bridge.getSpec().getId())));
        expectedKafka.getSpec().getNet().getSasl().getUser().getSecretKeyRef().setName(bridge.getSpec().getId());
        expectedKafka.getSpec().getNet().getSasl().getPassword().getSecretKeyRef().setName(bridge.getSpec().getId());

        expectedKafka.getSpec().getSink().getRef().setName(bridge.getSpec().getId());

        KafkaSource kafkaSource = kubernetesClient
                .resources(KafkaSource.class)
                .inNamespace(namespace)
                .withName(expectedKafka.getMetadata().getName())
                .get();

        if (kafkaSource == null) {
            return kubernetesClient
                    .resources(KafkaSource.class)
                    .inNamespace(namespace)
                    .create(expectedKafka);
        }

        return kafkaSource;
    }
}
