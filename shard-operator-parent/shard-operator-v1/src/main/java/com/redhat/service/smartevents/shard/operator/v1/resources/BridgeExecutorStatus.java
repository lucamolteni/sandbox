package com.redhat.service.smartevents.shard.operator.v1.resources;

import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redhat.service.smartevents.infra.v1.api.models.ManagedResourceStatusV1;
import com.redhat.service.smartevents.shard.operator.core.resources.Condition;
import com.redhat.service.smartevents.shard.operator.core.resources.ConditionReasonConstants;
import com.redhat.service.smartevents.shard.operator.core.resources.ConditionStatus;
import com.redhat.service.smartevents.shard.operator.core.resources.ConditionTypeConstants;
import com.redhat.service.smartevents.shard.operator.core.resources.CustomResourceStatus;
import com.redhat.service.smartevents.shard.operator.core.utils.DeploymentStatusUtils;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;

public class BridgeExecutorStatus extends CustomResourceStatus {

    public static final String SECRET_AVAILABLE = "SecretAvailable";
    public static final String IMAGE_NAME_CORRECT = "ImageNameCorrect";
    public static final String DEPLOYMENT_AVAILABLE = "DeploymentAvailable";
    public static final String SERVICE_AVAILABLE = "ServiceAvailable";
    public static final String SERVICE_MONITOR_AVAILABLE = "ServiceMonitorAvailable";

    private static final HashSet<Condition> EXECUTOR_CONDITIONS = new HashSet<>() {
        {
            add(new Condition(ConditionTypeConstants.AUGMENTING, ConditionStatus.Unknown));
            add(new Condition(ConditionTypeConstants.READY, ConditionStatus.Unknown));
            add(new Condition(SECRET_AVAILABLE, ConditionStatus.Unknown));
            add(new Condition(IMAGE_NAME_CORRECT, ConditionStatus.Unknown));
            add(new Condition(DEPLOYMENT_AVAILABLE, ConditionStatus.Unknown));
            add(new Condition(SERVICE_AVAILABLE, ConditionStatus.Unknown));
            add(new Condition(SERVICE_MONITOR_AVAILABLE, ConditionStatus.Unknown));
        }
    };

    public BridgeExecutorStatus() {
        super(EXECUTOR_CONDITIONS);
    }

    public ManagedResourceStatusV1 inferManagedResourceStatus() {
        if (isReady()) {
            return ManagedResourceStatusV1.READY;
        }
        if (!isAugmentingTrueOrUnknown()) {
            return ManagedResourceStatusV1.FAILED;
        }
        return ManagedResourceStatusV1.PROVISIONING;
    }

    @JsonIgnore
    public final void setStatusFromDeployment(Deployment deployment) {
        if (deployment.getStatus() == null) {
            if (!isConditionTypeFalse(ConditionTypeConstants.READY, ConditionReasonConstants.DEPLOYMENT_NOT_AVAILABLE)) {
                markConditionFalse(ConditionTypeConstants.READY,
                        ConditionReasonConstants.DEPLOYMENT_NOT_AVAILABLE,
                        "");
            }
            if (!isConditionTypeFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE)) {
                markConditionFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE);
            }
        } else if (Readiness.isDeploymentReady(deployment)) {
            if (!isConditionTypeFalse(ConditionTypeConstants.READY, ConditionReasonConstants.DEPLOYMENT_AVAILABLE)) {
                markConditionFalse(ConditionTypeConstants.READY,
                        ConditionReasonConstants.DEPLOYMENT_AVAILABLE,
                        "");
            }
            if (!isConditionTypeTrue(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE)) {
                markConditionTrue(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE);
            }
        } else {
            if (DeploymentStatusUtils.isTimeoutFailure(deployment)) {
                if (!isConditionTypeFalse(ConditionTypeConstants.READY, ConditionReasonConstants.DEPLOYMENT_FAILED)) {
                    markConditionFalse(ConditionTypeConstants.READY,
                            ConditionReasonConstants.DEPLOYMENT_FAILED,
                            DeploymentStatusUtils.getReasonAndMessageForTimeoutFailure(deployment));
                }
                if (!isConditionTypeFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE)) {
                    markConditionFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE);
                }
            } else if (DeploymentStatusUtils.isStatusReplicaFailure(deployment)) {
                if (!isConditionTypeFalse(ConditionTypeConstants.READY, ConditionReasonConstants.DEPLOYMENT_FAILED)) {
                    markConditionFalse(ConditionTypeConstants.READY,
                            ConditionReasonConstants.DEPLOYMENT_FAILED,
                            DeploymentStatusUtils.getReasonAndMessageForReplicaFailure(deployment));
                }
                if (!isConditionTypeFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE)) {
                    markConditionFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE);
                }
            } else {
                if (!isConditionTypeFalse(ConditionTypeConstants.READY, ConditionReasonConstants.DEPLOYMENT_NOT_AVAILABLE)) {
                    markConditionFalse(ConditionTypeConstants.READY,
                            ConditionReasonConstants.DEPLOYMENT_NOT_AVAILABLE,
                            "");
                }
                if (!isConditionTypeFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE)) {
                    markConditionFalse(BridgeExecutorStatus.DEPLOYMENT_AVAILABLE);
                }
            }
        }
    }
}
