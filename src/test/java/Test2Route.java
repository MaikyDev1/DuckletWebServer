import eu.duckee.duckletwebserver.annotations.http_types.GetRequest;
import eu.duckee.duckletwebserver.annotations.http_types.PutRequest;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.annotations.request.RequestUrlParam;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;

@RequestMapping ("/test")
public class Test2Route {

    @GetRequest
    @RequestMapping("/from_class")
    public DuckletResponse oki() {
        return DuckletResponse.ok().sendJson("ok", "OKKK1");
    }

    @PutRequest
    @RequestMapping("/miaw/[id]")
    public DuckletResponse postok(@RequestUrlParam("id") Integer id) {
        return DuckletResponse.ok().sendJson("ok", id);
    }

}
