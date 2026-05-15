package eu.duckee.internal;

import eu.duckee.duckletwebserver.annotations.http_types.GetRequest;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.annotations.request.RequestParam;
import eu.duckee.duckletwebserver.annotations.request.RequestUrlParam;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;

@RequestMapping("/api/v1")
public class ExampleRoute {

    @GetRequest
    @RequestMapping("/test/[id]")
    public DuckletResponse test(@RequestUrlParam("id") String id, @RequestParam("hey") String hey) {
        System.out.println(id);
        System.out.println(hey);
        return DuckletResponse.ok().sendText("ok!");
    }

}
