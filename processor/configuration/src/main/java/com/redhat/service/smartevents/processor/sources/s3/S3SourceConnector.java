package com.redhat.service.smartevents.processor.sources.s3;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.redhat.service.smartevents.infra.models.connectors.ConnectorType;
import com.redhat.service.smartevents.infra.models.gateways.Source;
import com.redhat.service.smartevents.processor.AbstractGatewayConnector;

@ApplicationScoped
public class S3SourceConnector extends AbstractGatewayConnector<Source> implements S3Source {

    public static final ConnectorType CONNECTOR_TYPE = ConnectorType.SOURCE;
    public static final String CONNECTOR_TYPE_ID = "aws_s3_source_0.1";

    public static final String S3_AWS_BUCKET_NAME_OR_ARN_PARAMETER = "aws_bucket_name_or_arn";
    public static final String S3_AWS_REGION_PARAMETER = "aws_region";
    public static final String S3_AWS_ACCESS_KEY_PARAMETER = "aws_access_key";
    public static final String S3_AWS_SECRET_KEY_PARAMETER = "aws_secret_key";
    public static final String S3_AWS_IGNORE_BODY_PARAMETER = "aws_ignore_body";
    public static final String S3_AWS_DELETE_AFTER_READ_PARAMETER = "aws_delete_after_read";

    public S3SourceConnector() {
        super(CONNECTOR_TYPE, CONNECTOR_TYPE_ID);
    }

    @Override
    protected void addConnectorSpecificPayload(Source source, String topicName, ObjectNode definition) {
        Map<String, String> sourceParameters = source.getParameters();

        definition.set(CONNECTOR_TOPIC_PARAMETER, new TextNode(topicName));

        definition.set(S3_AWS_BUCKET_NAME_OR_ARN_PARAMETER, new TextNode(sourceParameters.get(BUCKET_NAME_OR_ARN_PARAMETER)));
        definition.set(S3_AWS_REGION_PARAMETER, new TextNode(sourceParameters.get(REGION_PARAMETER)));
        definition.set(S3_AWS_ACCESS_KEY_PARAMETER, new TextNode(sourceParameters.get(ACCESS_KEY_PARAMETER)));
        definition.set(S3_AWS_SECRET_KEY_PARAMETER, new TextNode(sourceParameters.get(SECRET_KEY_PARAMETER)));
        definition.set(S3_AWS_IGNORE_BODY_PARAMETER, new TextNode(sourceParameters.get(IGNORE_BODY_PARAMETER)));
        definition.set(S3_AWS_DELETE_AFTER_READ_PARAMETER, BooleanNode.valueOf(Boolean.parseBoolean(sourceParameters.get(DELETE_AFTER_READ_PARAMETER))));
    }
}
