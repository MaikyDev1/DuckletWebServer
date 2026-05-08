package eu.duckee.duckletwebserver.utils;

import eu.duckee.duckletwebserver.exception.MappingParserException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping {

    private List<Pair<String, Boolean>> urlPath;
    @Getter
    private String fastUrl = null;
    @Getter
    private boolean dynamic = false;

    public static Mapping wrapFromString(String mapping) throws MappingParserException {
        Mapping obj = new Mapping();
        String validUrl = obj.convertToValidUrl(mapping);
        String[] parts = validUrl.equalsIgnoreCase("/") ? new String[]{"/"} : validUrl.split("/");
        obj.urlPath = new ArrayList<>();
        for (String s : parts) {
            if (s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']') {
                obj.urlPath.add(new Pair<>(s.substring(1, s.length() - 1) , true));
                obj.dynamic = true;
            } else {
                obj.urlPath.add(new Pair<>(s, false));
            }
        }
        if (!obj.dynamic)
            obj.fastUrl = validUrl;
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

    public boolean match(String url) {
        String validUrl = convertToValidUrl(url);
        if (!dynamic)
            return validUrl.equalsIgnoreCase(fastUrl);
        String[] parsedUrl = validUrl.split("/");
        if (parsedUrl.length != urlPath.size())
            return false;
        for (int i = 0; i < parsedUrl.length; i++) {
            if (!parsedUrl[i].equalsIgnoreCase(urlPath.get(i).getKey()) && !urlPath.get(i).getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param url
     * @return Return a pair between by a true -> Its a match. false -> Not a match
     */
    public Pair<Boolean, Map<String, String>> matchAndGetTagLines(String url) {
        String validUrl = convertToValidUrl(url);
        if (!dynamic)
            return new Pair<>(validUrl.equalsIgnoreCase(fastUrl), null);
        String[] parsedUrl = validUrl.split("/");
        Map<String, String> tagLines = null;
        if (parsedUrl.length != urlPath.size())
            return new Pair<>(false, null);
        for (int i = 0; i < parsedUrl.length; i++) {
            if (!parsedUrl[i].equalsIgnoreCase(urlPath.get(i).getKey()) && !urlPath.get(i).getValue()) {
                return new Pair<>(false, null);
            }
            if (urlPath.get(i).getValue()) {
                if (tagLines == null) tagLines = new HashMap<>();
                tagLines.put(urlPath.get(i).getKey(), parsedUrl[i]);
            }
        }
        return new Pair<>(true, tagLines);
    }

}
