package eu.duckee.duckletwebserver.utils;

import eu.duckee.duckletwebserver.exception.MappingParserException;
import lombok.Getter;

import java.util.*;

/**
 * The mapping class is a helper for endpoints url path, why exists and how it 
 * treats the dynamic part of the URLs (for example: [id]). 
 * Not all the routes are dynamic so if a route will contain no [(something)] we store the url as it is
 * and compare it when is need it.
 * If the route is dynamic we will use the record UrlSegments to store each part and the dynamic ones are marked
 * fastUrl will be ignored, at any mach we will need to loop through all the parts.
 */
public class Mapping {

    public record UrlSegments (String name, Boolean isDynamic) {};

    private List<UrlSegments> urlPath;
    @Getter
    private String fastUrl = null;
    @Getter
    private boolean dynamic = false;

    /**
     * Map a string to a Mapping object with all its proprieties.
     * @param mapping A url. Just the part from /....
     * @return The mapping object
     * @throws MappingParserException It might fail at parsing so a parser exception could be thrown
     */
    public static Mapping wrapFromString(String mapping) throws MappingParserException {
        Mapping obj = new Mapping();
        String validUrl = obj.convertToValidUrl(mapping);
        if (validUrl.equals("/")) {
            obj.fastUrl = validUrl;
            return obj;
        }
        /* check for no dynamic routes so we can use fastUrl logic */
        if (!(validUrl.contains("[") && validUrl.contains("]"))) {
            obj.fastUrl = validUrl;
            return obj;
        }
        /* Here the dynamic part is hit so we must use dynamic routes */
        String[] parts = validUrl.split("/");
        obj.urlPath = new ArrayList<>();
        for (String s : parts) {
            if (s.isEmpty()) {
                obj.urlPath.add(new UrlSegments("", false));
            } else if (s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']') {
                obj.urlPath.add(new UrlSegments(s.substring(1, s.length() - 1), true));
                obj.dynamic = true;
            } else {
                obj.urlPath.add(new UrlSegments(s, false));
            }
        }
        return obj;
    }

    private String convertToValidUrl(String url) {
        if (url.length() <= 1)
            return "/";
        if (url.charAt(0) == '/')
            url = url.substring(1);
        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);
        return url;
    }

    /**
     * Match a string to this mapping.
     * If you need information about the dynamic segments please see {@link #matchAndExtract(String)}
     * @param url a URL
     * @return true if the match is good false otherwise
     */
    public boolean match(String url) {
        String validUrl = convertToValidUrl(url);
        if (!dynamic)
            return validUrl.equals(fastUrl);
        String[] parsedUrl = validUrl.split("/");
        if (parsedUrl.length != urlPath.size())
            return false;
        for (int i = 0; i < parsedUrl.length; i++) {
            if (urlPath.get(i).isDynamic)
                continue;
            if (!parsedUrl[i].equals(urlPath.get(i).name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This function is used when you care about the dynamic tag lines.
     * {@link #match(String)} will just give you if the url matches and no taglines.
     *
     * @param url the exchange url
     * @return a <b>non-empty</b> optional with a map if the url matches,
     *         or a <b>EMPTY</b> optional if the url did no match
     */
    public Optional<Map<String, String>> matchAndExtract(String url) {
        String validUrl = convertToValidUrl(url);
        if (!dynamic) {
            if (validUrl.equals(fastUrl))
                return Optional.of(Map.of());
        } else {
            String[] parsedUrl = validUrl.split("/");
            if (parsedUrl.length == urlPath.size()) {
                Map<String, String> tagLines = new HashMap<>();
                for (int i = 0; i < parsedUrl.length; i++) {
                    if (urlPath.get(i).isDynamic) {
                        tagLines.put(urlPath.get(i).name, parsedUrl[i]);
                    } else if (!parsedUrl[i].equals(urlPath.get(i).name)) {
                        return Optional.empty();
                    }
                }
                return Optional.of(tagLines);
            }
        }
        return Optional.empty();
    }

}
