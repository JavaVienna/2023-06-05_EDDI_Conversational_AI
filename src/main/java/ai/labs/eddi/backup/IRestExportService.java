package ai.labs.eddi.backup;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author ginccc
 */
@Path("/backup/export")
public interface IRestExportService {
    @GET
    @Produces("application/zip")
    @Path("{botFilename}")
    Response getBotZipArchive(@PathParam("botFilename") String botFilename);

    @POST
    @Path("{botId}")
    Response exportBot(@PathParam("botId") String botId,
                       @QueryParam("botVersion") @DefaultValue("1") Integer botVersion);
}
