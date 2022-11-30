package com.redhat.service.smartevents.shard.operator.v2.providers;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.service.smartevents.shard.operator.core.providers.TemplateImportConfig;
import com.redhat.service.smartevents.shard.operator.core.utils.LabelsBuilder;
import com.redhat.service.smartevents.shard.operator.v2.TestSupport;
import com.redhat.service.smartevents.shard.operator.v2.resources.CamelIntegration;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedProcessor;

import io.fabric8.knative.eventing.contrib.kafka.v1beta1.KafkaSASLSpec;
import io.fabric8.knative.eventing.contrib.kafka.v1beta1.KafkaSource;
import io.fabric8.knative.eventing.contrib.kafka.v1beta1.KafkaSourceSpec;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.client.CustomResource;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateProviderImplTest {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final ManagedProcessor MANAGED_PROCESSOR = ManagedProcessor.fromBuilder()
            .withProcessorName("id")
            .withNamespace("ns")
            .withBridgeId(TestSupport.BRIDGE_ID)
            .withCustomerId(TestSupport.CUSTOMER_ID)
            .withProcessorId("id")
            .withDefinition(objectMapper.createObjectNode())
            .build();

    @Test
    public void metadataIsUpdated() {
        TemplateProvider templateProvider = new TemplateProviderImpl();
        CamelIntegration camelIntegration = templateProvider.loadCamelIntegrationTemplate(MANAGED_PROCESSOR, TemplateImportConfig.withDefaults());
        assertOwnerReference(MANAGED_PROCESSOR, camelIntegration.getMetadata());
        assertThat(camelIntegration.getMetadata().getName()).isEqualTo(MANAGED_PROCESSOR.getMetadata().getName());
        assertThat(camelIntegration.getMetadata().getNamespace()).isEqualTo(MANAGED_PROCESSOR.getMetadata().getNamespace());
    }

    @Test
    public void bridgeCamelIntegrationTemplateIsProvided() {
        TemplateProvider templateProvider = new TemplateProviderImpl();
        CamelIntegration camelIntegration = templateProvider.loadCamelIntegrationTemplate(MANAGED_PROCESSOR, TemplateImportConfig.withDefaults());

        assertOwnerReference(MANAGED_PROCESSOR, camelIntegration.getMetadata());
        assertLabels(camelIntegration.getMetadata(), MANAGED_PROCESSOR.COMPONENT_NAME);
        assertThat(camelIntegration.getMetadata().getName()).isEqualTo("proc-id");
        assertThat(camelIntegration.getMetadata().getNamespace()).isEqualTo("ns");
        assertThat(camelIntegration.getSpec().getFlows()).isEqualTo(new ArrayList<>());
    }

    @Test
    public void kafkaSourceTemplateIsProvided() {
        TemplateProvider templateProvider = new TemplateProviderImpl();
        KafkaSource kafkaSource = templateProvider.loadKafkaSourceTemplate(MANAGED_PROCESSOR, TemplateImportConfig.withDefaults());

        ObjectMeta metadata = kafkaSource.getMetadata();
        assertOwnerReference(MANAGED_PROCESSOR, metadata);
        assertLabels(metadata, MANAGED_PROCESSOR.COMPONENT_NAME);

        assertThat(metadata.getName()).isEqualTo("proc-id");
        assertThat(metadata.getNamespace()).isEqualTo("ns");

        KafkaSourceSpec spec = kafkaSource.getSpec();
        assertThat(spec.getConsumerGroup()).isBlank();
        assertThat(spec.getBootstrapServers()).isEmpty();
        assertThat(spec.getTopics()).isEmpty();

        KafkaSASLSpec sasl = spec.getNet().getSasl();
        assertThat(sasl.getUser().getSecretKeyRef().getKey()).isBlank();
        assertThat(sasl.getPassword().getSecretKeyRef().getKey()).isBlank();

        assertThat(spec.getSink().getRef().getName()).isBlank();

    }

    private void assertLabels(ObjectMeta meta, String component) {
        assertThat(meta.getLabels().get(LabelsBuilder.COMPONENT_LABEL)).isEqualTo(component);
        assertThat(meta.getLabels().get(LabelsBuilder.MANAGED_BY_LABEL)).isEqualTo(LabelsBuilder.OPERATOR_NAME);
        assertThat(meta.getLabels().get(LabelsBuilder.CREATED_BY_LABEL)).isEqualTo(LabelsBuilder.OPERATOR_NAME);
    }

    private void assertOwnerReference(CustomResource resource, ObjectMeta meta) {
        assertThat(meta.getOwnerReferences().size()).isEqualTo(1);

        OwnerReference ownerReference = meta.getOwnerReferences().get(0);
        assertThat(ownerReference.getName()).isEqualTo(resource.getMetadata().getName());
        assertThat(ownerReference.getApiVersion()).isEqualTo(resource.getApiVersion());
        assertThat(ownerReference.getKind()).isEqualTo(resource.getKind());
        assertThat(ownerReference.getUid()).isEqualTo(resource.getMetadata().getUid());
    }
}
