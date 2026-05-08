package eu.duckee.duckletwebserver.exchange;

import eu.duckee.duckletwebserver.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DuckletRequest {

    @Setter
    private String url;
    private Map<String, String> httpParams;
    private Map<String, String> tagLines;
    @Setter
    private HttpMethod method;
    @Setter
    private String httpBody;
    private List<Cookie> cookies;
    private List<HttpHeader> headers;

    public DuckletRequest addHttpParams(Map<String, String> params) {
        if (params == null)
            return this;
        this.httpParams = new HashMap<>(params);
        return this;
    }

    public DuckletRequest addHttpParam(String key, String value) {
        if (this.httpParams == null)
            this.httpParams = new HashMap<>();
        this.httpParams.put(key, value);
        return this;
    }

    public DuckletRequest addTagLines(Map<String, String> tags) {
        if (tags == null)
            return this;
        this.tagLines = new HashMap<>(tags);
        return this;
    }

    public DuckletRequest addTagLine(String key, String value) {
        if (this.tagLines == null)
            this.tagLines = new HashMap<>();
        this.tagLines.put(key, value);
        return this;
    }

    public DuckletRequest addACookie(Cookie cookie) {
        if (this.cookies == null)
            this.cookies = new ArrayList<>();
        this.cookies.add(cookie);
        return this;
    }

    public URI computeURI() {
        if (this.httpParams == null || this.httpParams.isEmpty())
            return URI.create(url);

        StringBuilder newUrl = new StringBuilder(url);
        newUrl.append("?");

        for(String key : this.httpParams.keySet()) {
            newUrl.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(this.httpParams.get(key), StandardCharsets.UTF_8)).append("&");
        }
        return URI.create(newUrl.toString());
    }

    public DuckletResponse makeHttpRequest() throws IOException, InterruptedException {
        URI uri = computeURI();
        HttpRequest.Builder request = HttpRequest.newBuilder().uri(uri);
        if (headers != null)
            headers.forEach(hdr -> request.setHeader(hdr.name(), hdr.value()));
        HttpResponse<String> response = HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
        DuckletResponse duckletResponse = new DuckletResponse();
        duckletResponse.setCode(response.statusCode());
        duckletResponse.sendJson(response.body());
        return duckletResponse;
    }

}
