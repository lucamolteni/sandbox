package com.redhat.service.smartevents.manager.v2.api.user;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.redhat.service.smartevents.infra.core.models.responses.ErrorsResponse;
import com.redhat.service.smartevents.infra.v2.api.V2APIConstants;
import com.redhat.service.smartevents.infra.v2.api.models.queries.QueryResourceInfo;
import com.redhat.service.smartevents.manager.v2.api.user.models.requests.ConnectorRequest;
import com.redhat.service.smartevents.manager.v2.api.user.models.responses.SinkConnectorListResponse;
import com.redhat.service.smartevents.manager.v2.api.user.models.responses.SinkConnectorResponse;

import io.quarkus.security.Authenticated;

@Tag(name = "Sink Connectors", description = "The API that allow the user to retrieve, create or delete Sink Connectors to be used with smart-events.")
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "bearer",
                type = SecuritySchemeType.HTTP,
                scheme = "Bearer")
})
@SecurityRequirement(name = "bearer")
@Path(V2APIConstants.V2_USER_API_BASE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RegisterRestClient
public class SinkConnectorsAPI {

    @APIResponses(value = {
            @APIResponse(description = "Success.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SinkConnectorResponse.class))),
            @APIResponse(description = "Bad request.", responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Unauthorized.", responseCode = "401"),
            @APIResponse(description = "Forbidden.", responseCode = "403"),
            @APIResponse(description = "Not found.", responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Internal error.", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @Operation(summary = "Get a Sink Connector of a Bridge instance", description = "Get a Sink Connector of a Bridge instance for the authenticated user.")
    @GET
    @Path("{bridgeId}/sinks/{sinkId}")
    public Response getSinkConnector(@NotEmpty @PathParam("bridgeId") String bridgeId, @NotEmpty @PathParam("sinkId") String sinkId) {
        return Response.status(500, "Not implemented yet.").build();
    }

    @APIResponses(value = {
            @APIResponse(description = "Success.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SinkConnectorListResponse.class))),
            @APIResponse(description = "Bad request.", responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Unauthorized.", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Forbidden.", responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Not found.", responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Internal error.", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @Operation(summary = "Get the list of Sink Connectors for a Bridge", description = "Get the list of Sink Connector instances of a Bridge instance for the authenticated user.")
    @GET
    @Path("{bridgeId}/sinks")
    public Response getSinkConnectors(@NotEmpty @PathParam("bridgeId") String bridgeId, @Valid @BeanParam QueryResourceInfo queryInfo) {
        return Response.status(500, "Not implemented yet.").build();
    }

    @APIResponses(value = {
            @APIResponse(description = "Accepted.", responseCode = "202",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SinkConnectorResponse.class))),
            @APIResponse(description = "Bad request.", responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Unauthorized.", responseCode = "401"),
            @APIResponse(description = "Not enough quota.", responseCode = "402", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Forbidden.", responseCode = "403"),
            @APIResponse(description = "Not found.", responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Internal error.", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @Operation(summary = "Create a Sink Connector for a Bridge instance", description = "Create a Sink Connector of a Bridge instance for the authenticated user.")
    @POST
    @Path("{bridgeId}/sinks")
    public Response createSinkConnector(@NotEmpty @PathParam("bridgeId") String bridgeId, @Valid ConnectorRequest connectorRequest) {
        return Response.status(500, "Not implemented yet.").build();
    }

    @APIResponses(value = {
            @APIResponse(description = "Accepted.", responseCode = "202",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SinkConnectorResponse.class))),
            @APIResponse(description = "Bad request.", responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Unauthorized.", responseCode = "401"),
            @APIResponse(description = "Forbidden.", responseCode = "403"),
            @APIResponse(description = "Not found.", responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Internal error.", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @Operation(summary = "Update a Sink Connector instance.",
            description = "Update a Sink Connector instance for the authenticated user.")
    @PUT
    @Path("{bridgeId}/sinks/{sinkId}")
    public Response updateSinkConnector(@NotEmpty @PathParam("bridgeId") String bridgeId, @NotEmpty @PathParam("sinkId") String sinkId,
            @Valid ConnectorRequest connectorRequest) {
        return Response.status(500, "Not implemented yet.").build();
    }

    @APIResponses(value = {
            @APIResponse(description = "Accepted.", responseCode = "202"),
            @APIResponse(description = "Bad request.", responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Unauthorized.", responseCode = "401"),
            @APIResponse(description = "Forbidden.", responseCode = "403"),
            @APIResponse(description = "Not found.", responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class))),
            @APIResponse(description = "Internal error.", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @Operation(summary = "Delete a Sink Connector of a Bridge instance", description = "Delete a Sink Connector of a Bridge instance for the authenticated user.")
    @DELETE
    @Path("{bridgeId}/sinks/{sinkId}")
    public Response deleteSinkConnector(@PathParam("bridgeId") String bridgeId, @PathParam("sinkId") String sinkId) {
        return Response.status(500, "Not implemented yet.").build();
    }
}
