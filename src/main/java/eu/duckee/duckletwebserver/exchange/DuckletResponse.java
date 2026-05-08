package eu.duckee.duckletwebserver.exchange;

import com.sun.net.httpserver.HttpExchange;
import eu.duckee.duckletwebserver.ResponseType;
import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.json.objects.JsonEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class DuckletResponse {

    private int code;
    private String content;
    private ResponseType responseType = ResponseType.TEXT;

    // 200-299 - Successful messages

    public static DuckletResponse ok() {
        return new DuckletResponse().setCode(200);
    }

    public static DuckletResponse created() {
        return new DuckletResponse().setCode(201);
    }

    public static DuckletResponse accepted() {
        return new DuckletResponse().setCode(202);
    }

    public static DuckletResponse noContent() {
        return new DuckletResponse().setCode(204);
    }

    // 400-499 - Client error requests

    public static DuckletResponse badRequest() {
        return new DuckletResponse().setCode(400);
    }
    public static DuckletResponse notFound() {
        return new DuckletResponse().setCode(404);
    }

    // 500
    public static DuckletResponse internalServerError() {
        return new DuckletResponse().setCode(500);
    }

    public DuckletResponse sendJson(String json) {
        this.responseType = ResponseType.JSON;
        this.content = json;
        return this;
    }

    public DuckletResponse sendJson(JsonEntity json) {
        this.responseType = ResponseType.JSON;
        content = DuckyJson.deserialization(json);
        return this;
    }

    public DuckletResponse sendJson(Map<String, Object> json) {
        this.responseType = ResponseType.JSON;
        return this;
    }

    public DuckletResponse sendJson(List<Object> json) {
        this.responseType = ResponseType.JSON;
        return this;
    }

    public DuckletResponse sendJson(String k1, Object v1) {
        this.responseType = ResponseType.JSON;
        String v1JsonReady;
        if (v1 instanceof Number)
            v1JsonReady = v1.toString();
        else v1JsonReady = "\"" + v1.toString() + "\"";
        content = "{\"" + k1 + "\": " + v1JsonReady + "}";
        return this;
    }

    public DuckletResponse sendText(String text) {
        this.content = text;
        this.responseType = ResponseType.TEXT;
        return this;
    }

    public DuckletResponse addCookie() {
        return this;
    }

    public void respond(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", responseType.getContentType());
        exchange.sendResponseHeaders(code, content.length());
        OutputStream os = exchange.getResponseBody();
        os.write(content.getBytes());
        os.close();
    }

    // semi builder

    public DuckletResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public DuckletResponse setContent(String content) {
        this.content = content;
        return this;
    }

    public DuckletResponse setResponseType(ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }
}
