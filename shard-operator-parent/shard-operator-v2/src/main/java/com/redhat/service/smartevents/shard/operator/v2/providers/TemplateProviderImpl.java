package com.redhat.service.smartevents.shard.operator.v2.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.service.smartevents.shard.operator.core.providers.TemplateImportConfig;
import com.redhat.service.smartevents.shard.operator.core.utils.LabelsBuilder;
import com.redhat.service.smartevents.shard.operator.v2.resources.CamelIntegration;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.utils.Serialization;

@ApplicationScoped
public class TemplateProviderImpl implements TemplateProvider {

    private static final String TEMPLATES_DIR = "/templates";
    private static final String BRIDGE_INGRESS_OPENSHIFT_ROUTE_PATH = TEMPLATES_DIR + "/camel-integration.yaml";

    @Override
    public CamelIntegration loadCamelIntegrationTemplate(CustomResource owner, TemplateImportConfig config) {
        CamelIntegration camelIntegration = loadYaml(CamelIntegration.class, BRIDGE_INGRESS_OPENSHIFT_ROUTE_PATH);
        ObjectMeta metadata = camelIntegration.getMetadata();
        updateMetadata(owner, metadata, config);
        return camelIntegration;
    }

    private <T> T loadYaml(Class<T> clazz, String yaml) {
        try (InputStream is = TemplateProviderImpl.class.getResourceAsStream(yaml)) {
            return Serialization.unmarshal(is, clazz);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot find yaml on classpath: " + yaml);
        }
    }

    private void updateMetadata(CustomResource owner, ObjectMeta meta, TemplateImportConfig config) {
        // Name and namespace
        meta.setName(config.isNameToBeSet() ? owner.getMetadata().getName() : null);
        meta.setNamespace(config.isNamespaceToBeSet() ? owner.getMetadata().getNamespace() : null);

        // Owner reference
        if (config.isOwnerReferencesToBeSet()) {
            meta.getOwnerReferences().get(0).setKind(owner.getKind());
            meta.getOwnerReferences().get(0).setName(owner.getMetadata().getName());
            meta.getOwnerReferences().get(0).setApiVersion(owner.getApiVersion());
            meta.getOwnerReferences().get(0).setUid(owner.getMetadata().getUid());
        } else {
            meta.setOwnerReferences(null);
        }

        // Primary resource
        if (config.isPrimaryResourceToBeSet()) {
            Map<String, String> annotations = new LabelsBuilder()
                    .withPrimaryResourceName(owner.getMetadata().getName())
                    .withPrimaryResourceNamespace(owner.getMetadata().getNamespace())
                    .build();
            if (meta.getAnnotations() == null) {
                meta.setAnnotations(annotations);
            } else {
                meta.getAnnotations().putAll(annotations);
            }
        }
    }
}
