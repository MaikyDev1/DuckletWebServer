package eu.duckee.duckletwebserver.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpTools {

    /**
     * Simple query params extractor from URLS
     * The input must be the portion of the URL after the '?'
     * @param queryString The query url string
     * @return null if there is no query params or a map with them
     */
    public static Map<String, String> computeQueryParams(String queryString) {
        if (queryString == null || queryString.isEmpty())
            return null;
        Map<String, String> query = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            query.put(
                    URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                    keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : ""
            );
        }
        return query;
    }

}
