package com.redhat.service.smartevents.shard.operator.v2;

import java.time.Duration;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.service.smartevents.infra.core.api.dto.KafkaConnectionDTO;
import com.redhat.service.smartevents.infra.v2.api.models.OperationType;
import com.redhat.service.smartevents.infra.v2.api.models.dto.BridgeDTO;
import com.redhat.service.smartevents.infra.v2.api.models.dto.ProcessorDTO;
import com.redhat.service.smartevents.shard.operator.v2.resources.CamelIntegration;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedBridge;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedProcessor;
import com.redhat.service.smartevents.test.resource.KeycloakResource;

import io.fabric8.knative.eventing.contrib.kafka.v1beta1.KafkaSource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithOpenShiftTestServer;

import static com.redhat.service.smartevents.shard.operator.v2.utils.AwaitilityUtil.await;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithOpenShiftTestServer
@QuarkusTestResource(value = KeycloakResource.class, restrictToAnnotatedClass = true)
public class ManagedProcessorServiceImplTest {

    @Inject
    ManagedProcessorService bridgeIngressService;

    @Inject
    KubernetesClient kubernetesClient;

    @Inject
    ObjectMapper objectMapper;

    private ObjectNode EXAMPLE_FLOW;
    private ProcessorDTO EXAMPLE_PROCESSOR_DTO;
    private BridgeDTO EXAMPLE_BRIDGE_DTO;

    @BeforeEach
    public void setup() throws JsonProcessingException {

        EXAMPLE_FLOW = objectMapper.readValue("{\"from\":{\"uri\":\"fromURI\",\"steps\":[{\"to\":\"toURI\"}]}}", ObjectNode.class);
        EXAMPLE_PROCESSOR_DTO = new ProcessorDTO("processorId", "processorName", EXAMPLE_FLOW, "bridgeId", "customerId", "owner", OperationType.CREATE);

        KafkaConnectionDTO kafkaConnectionDTO = new KafkaConnectionDTO("bootstrapserver:443", "clientId", "clientSecret", "securityProtocol", "saslMechanism", "sourceTopic", "errorTopic");
        EXAMPLE_BRIDGE_DTO = new BridgeDTO("bridgeId", "bridgeName", "https://exampleendpoint", "tlsCertificate", "tlsKey", "customerId", "owner", kafkaConnectionDTO, OperationType.CREATE);

        // Far from ideal... but each test assumes there are no other BridgeIngress instances in existence.
        // Unfortunately, however, some tests only check that provisioning either progressed to a certain
        // point of failed completely. There is therefore a good chance there's an incomplete BridgeIngress
        // in k8s when a subsequent test starts. This leads to non-deterministic behaviour of tests.
        // This ensures each test has a "clean" k8s environment.
        await(Duration.ofMinutes(1),
                Duration.ofSeconds(10),
                () -> assertThat(kubernetesClient.resources(ManagedBridge.class).inAnyNamespace().list().getItems().isEmpty()).isTrue());
    }

    @Test
    public void testCamelIntegrationIsProvisioned() {
        String namespace = "namespace";
        String processorName = ManagedProcessor.resolveResourceName(EXAMPLE_PROCESSOR_DTO.getId());

        bridgeIngressService.createManagedProcessor(EXAMPLE_PROCESSOR_DTO, namespace);
        waitUntilResourceExist(namespace, processorName, ManagedProcessor.class);
        ManagedProcessor managedProcessor = kubernetesClient
                .resources(ManagedProcessor.class)
                .inNamespace(namespace)
                .withName(processorName)
                .get();

        String integrationName = "integration-" + EXAMPLE_PROCESSOR_DTO.getName();
        CamelIntegration camelIntegration = bridgeIngressService.fetchOrCreateCamelIntegration(managedProcessor, integrationName);

        assertThat(camelIntegration).isNotNull();
        assertThat(camelIntegration.getSpec().getFlows().get(0)).isEqualTo(EXAMPLE_FLOW);
        waitUntilResourceExist(namespace, integrationName, CamelIntegration.class);
    }

    @Test
    public void testKNativeSourceIsProvisioned() {
        String namespace = "namespace";
        String processorName = ManagedProcessor.resolveResourceName(EXAMPLE_PROCESSOR_DTO.getId());

        bridgeIngressService.createManagedProcessor(EXAMPLE_PROCESSOR_DTO, namespace);
        waitUntilResourceExist(namespace, processorName, ManagedProcessor.class);

        ManagedProcessor managedProcessor = kubernetesClient
                .resources(ManagedProcessor.class)
                .inNamespace(namespace)
                .withName(ManagedProcessor.resolveResourceName(EXAMPLE_PROCESSOR_DTO.getId()))
                .get();

        ManagedBridge managedBridge = ManagedBridge.fromDTO(EXAMPLE_BRIDGE_DTO, namespace);

        KafkaSource kafkaSource = bridgeIngressService.fetchOrCreateKafkaSource(managedProcessor, managedBridge);
        waitUntilResourceExist(namespace, processorName, KafkaSource.class);

        assertThat(kafkaSource).isNotNull();
        assertThat(kafkaSource.getMetadata().getName()).isEqualTo(processorName);
        assertThat(kafkaSource.getSpec().getBootstrapServers()).containsOnly("bootstrapserver:443");
        assertThat(kafkaSource.getSpec().getConsumerGroup()).isEqualTo("proc-processorid-consumerGroup");
        assertThat(kafkaSource.getSpec().getTopics()).containsOnly("bridgeId-sourceTopic");
        assertThat(kafkaSource.getSpec().getNet().getSasl().getUser().getSecretKeyRef().getName()).isEqualTo("bridgeId");
        assertThat(kafkaSource.getSpec().getNet().getSasl().getPassword().getSecretKeyRef().getName()).isEqualTo("bridgeId");
        assertThat(kafkaSource.getSpec().getSink().getRef().getName()).isEqualTo("bridgeId");

    }

    private <T extends HasMetadata> void waitUntilResourceExist(String namespace, String name, Class<T> resourceType) {
        await(Duration.ofSeconds(30),
                Duration.ofMillis(200),
                () -> {
                    T resource = kubernetesClient
                            .resources(resourceType)
                            .inNamespace(namespace)
                            .withName(name)
                            .get();
                    assertThat(resource).isNotNull();
                });
    }
}
