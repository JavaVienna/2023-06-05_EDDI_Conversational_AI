package ai.labs.eddi.configs.deployment;

import ai.labs.eddi.configs.deployment.model.DeploymentInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author ginccc
 */
// @Api(value = "Bot Administration", authorizations = {@Authorization(value = "eddi_auth")})
@Path("/deploymentstore/deployments")
public interface IRestDeploymentStore {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Read deployment infos.")
    List<DeploymentInfo> readDeploymentInfos();
}
