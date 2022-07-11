package com.redhat.service.smartevents.shard.operator.camel;

import java.util.Optional;

import com.redhat.service.smartevents.shard.operator.resources.BridgeExecutor;
import com.redhat.service.smartevents.shard.operator.resources.camel.CamelIntegration;

import io.fabric8.kubernetes.api.model.Secret;

public interface CamelService {
    Optional<CamelIntegration> fetchOrCreateCamelIntegration(BridgeExecutor bridgeExecutor, Secret secret);

}
