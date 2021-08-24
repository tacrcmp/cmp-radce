package dp.config;

import dp.resources.CategorizationResource;
import dp.resources.ExporterResource;
import dp.resources.ImporterResource;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/rest")
public class RestConfig extends ResourceConfig {

    @PostConstruct
    public void registerResources() {
        register(CategorizationResource.class);
        register(ImporterResource.class);
        register(ExporterResource.class);

        register(LoggingFilter.class);
    }
}
