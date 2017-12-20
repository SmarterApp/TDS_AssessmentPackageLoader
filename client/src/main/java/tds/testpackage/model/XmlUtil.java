package tds.testpackage.model;

import java.util.Optional;

public class XmlUtil {
    /**
     * Utility to transform xml boolean value to java
     *
     * The xml values for a boolean type "0" and "1" are valid
     *
     * @param maybeValue xml value for a boolean
     * @param defaultValue boolean value if xml value is not present
     *
     * @return translated java boolean value from xml
     */
    public static boolean parseBoolean(final Optional<String> maybeValue, boolean defaultValue) {
        if (maybeValue.isPresent()) {
            final String value = maybeValue.get();
            switch (value) {
                case "0":
                    return false;
                case "1":
                    return true;
            }
            return Boolean.valueOf(value);
        }
        return defaultValue;
    }
}
