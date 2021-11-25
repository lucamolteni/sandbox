package com.redhat.service.bridge.manager.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.redhat.service.bridge.manager.models.ConnectorEntity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
@Transactional
public class ConnectorDAO implements PanacheRepositoryBase<ConnectorEntity, String> {

    private static final String IDS_PARAM = "ids";

    public ConnectorEntity findByProcessIdName(String processorId, String name) {
        Parameters p = Parameters.with(ConnectorEntity.NAME_PARAM, name).and(ConnectorEntity.PROCESSOR_ID_PARAM, processorId);
        return singleResultFromList(find("#CONNECTORENTITY.findByProcessorIdAndName", p));
    }

    private ConnectorEntity singleResultFromList(PanacheQuery<ConnectorEntity> find) {
        List<ConnectorEntity> processors = find.list();
        if (processors.size() > 1) {
            throw new IllegalStateException("Multiple Entities returned from a Query that should only return a single Entity");
        }
        return processors.size() == 1 ? processors.get(0) : null;
    }
}
