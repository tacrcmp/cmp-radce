package dp.resources;

import dp.services.Exporter;
import dp.services.Syncer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Component
@Path("")
public class ExporterResource {

    private final Exporter exporter;

    @Autowired
    public ExporterResource(Exporter exporter) {
        this.exporter = exporter;
    }

    @POST
    @Path("/export")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sync(String json) {
        JSONObject data = new JSONObject(json);

        if (Exporter.isExportReady()) {
            exporter.export(data);
            return Response.ok().build();
        }

        return Response.status(503).entity("Sync is not ready now.").build();
    }
}
