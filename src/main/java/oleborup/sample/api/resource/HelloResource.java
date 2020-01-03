package oleborup.sample.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    @GET
    public Map<String, String> hello() {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "world");
        return map;
    }

}
