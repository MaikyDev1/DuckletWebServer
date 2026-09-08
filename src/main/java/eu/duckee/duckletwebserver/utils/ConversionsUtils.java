package eu.duckee.duckletwebserver.utils;

import java.util.Locale;

public class ConversionsUtils {

    public static <T> T convert(Class<T> to, String value) {
        if (value == null) {
            return null;
        }

        try {
            Object result;

            if (to == String.class) {
                result = value;
            } else if (to == Integer.class) {
                result = Integer.valueOf(value);
            } else if (to == Long.class) {
                result = Long.valueOf(value);
            } else if (to == Double.class) {
                result = Double.valueOf(value);
            } else if (to == Float.class) {
                result = Float.valueOf(value);
            } else if (to == Byte.class) {
                result = Byte.valueOf(value);
            } else if (to == Boolean.class) {
                result = switch (value.toLowerCase(Locale.ROOT)) {
                    case "true", "1", "yes", "y", "on" -> true;
                    case "false", "0", "no", "n", "off" -> false;
                    default -> null;
                };
            } else {
                return null;
            }

            return to.cast(result);

        } catch (NumberFormatException e) {
            return null;
        }
    }

}
