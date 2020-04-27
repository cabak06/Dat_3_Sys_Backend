package cors;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

//Comment out the two annotations below to disable CORS-handling
@Provider  //This will ensure that the filter is used "automatically"
@PreMatching

/* Cross-origin resource sharing. CORS elements are used to define resource
sharing across "platforms", I.E using resources from other foreign origins,
while restricting yet another to protect the system.
CORS defines how and who can partake in this cross-sharing and aims to make
cross-sharing as secure as possible. */
public class CorsResponseFilter implements ContainerResponseFilter {
  private final static Logger LOG = Logger.getLogger(CorsResponseFilter.class.getName());
  @Override
  public void filter( ContainerRequestContext requestCtx, ContainerResponseContext res )
    throws IOException {
    // LOG.info( "Executing REST response filter" );
    res.getHeaders().add("Access-Control-Allow-Origin", "*" ); //allowing all domains
    res.getHeaders().add("Access-Control-Allow-Credentials", "true" );
    res.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT" );
    res.getHeaders().add("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, Authorization,x-access-token");
  }
}