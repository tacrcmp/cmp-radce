package dp.resources;

import dp.services.DataImporter;
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
public class ImporterResource {
    private final DataImporter dataImporter;

    @Autowired
    public ImporterResource(DataImporter dataImporter){
        this.dataImporter = dataImporter;
    }

    @POST
    @Path("/importData/{loadKeyWords}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response importData(@PathParam("loadKeyWords") boolean loadKeyWords,
                               String path) throws IOException, SQLException {
        dataImporter.importData(path, loadKeyWords);
        return Response.ok().build();
    }
}
