package com.redhat.service.smartevents.infra.core.exceptions.definitions.platform;

public class ConnectorGetException extends InternalPlatformException {

    public ConnectorGetException(String message) {
        super(message);
    }

    public ConnectorGetException(String message, Throwable cause) {
        super(message, cause);
    }
}
