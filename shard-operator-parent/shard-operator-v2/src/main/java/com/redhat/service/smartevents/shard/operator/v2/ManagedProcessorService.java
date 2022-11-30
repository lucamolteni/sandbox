package com.redhat.service.smartevents.shard.operator.v2;

import com.redhat.service.smartevents.infra.v2.api.models.dto.ProcessorDTO;
import com.redhat.service.smartevents.shard.operator.v2.resources.CamelIntegration;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedBridge;
import com.redhat.service.smartevents.shard.operator.v2.resources.ManagedProcessor;

import io.fabric8.knative.eventing.contrib.kafka.v1beta1.KafkaSource;

public interface ManagedProcessorService {

    void createManagedProcessor(ProcessorDTO bridgeDTO, String namespace);

    CamelIntegration fetchOrCreateCamelIntegration(ManagedProcessor processor, String integrationName);

    KafkaSource fetchOrCreateKafkaSource(ManagedProcessor processor, ManagedBridge managedBridge);
}
