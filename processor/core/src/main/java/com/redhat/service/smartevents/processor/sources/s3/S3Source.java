package com.redhat.service.smartevents.processor.sources.s3;

import com.redhat.service.smartevents.processor.GatewayBean;

public interface S3Source extends GatewayBean {

    String TYPE = "S3";

    String CLOUD_EVENT_TYPE = "S3Source";
    String BUCKET_NAME_OR_ARN_PARAMETER = "bucket_name_or_arn";
    String REGION_PARAMETER = "region";
    String ACCESS_KEY_PARAMETER = "access_key";
    String SECRET_KEY_PARAMETER = "secret_key";
    String IGNORE_BODY_PARAMETER = "ignore_body";
    String DELETE_AFTER_READ_PARAMETER = "delete_after_read";

    @Override
    default String getType() {
        return TYPE;
    }
}
