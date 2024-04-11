package oleborup.sample.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
