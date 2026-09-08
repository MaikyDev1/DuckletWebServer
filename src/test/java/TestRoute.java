import eu.duckee.duckletwebserver.annotations.http_types.GetRequest;
import eu.duckee.duckletwebserver.annotations.http_types.PostRequest;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;

@RequestMapping ("/test")
public class TestRoute {

    @GetRequest
    @RequestMapping("/miaw")
    public DuckletResponse oki() {
        return DuckletResponse.ok().sendJson("ok", "OKKK1");
    }

    @PostRequest
    @RequestMapping("/miaw")
    public DuckletResponse postok() {
        return DuckletResponse.ok().sendJson("ok", "POSTED");
    }

}
