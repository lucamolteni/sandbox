package com.redhat.service.smartevents.shard.operator.v2.resources;

public class TLSSpec {

    public TLSSpec(String key, String certificate) {
        this.certificate = certificate;
        this.key = key;
    }

    String certificate;

    String key;

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
