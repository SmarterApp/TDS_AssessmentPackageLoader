package tds.testpackage.model;

import java.util.Optional;

public class XmlUtil {
    protected static boolean parseBoolean(final String value) {
        switch (value) {
            case "0":
                return false;
            case "1":
                return true;
        }
        return Boolean.valueOf(value);
    }

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
        return maybeValue.map(XmlUtil::parseBoolean).orElse(defaultValue);
    }

    /**
     * Utility to handle hierarchy of default xml boolean values to java
     *
     * The xml values for a boolean type "0" and "1" are valid
     *
     * @param firstMaybeValue first xml value for a boolean, otherwise
     * @param firstMaybeValue second xml value for a boolean, otherwise
     * @param defaultValue boolean value if xml value is not present
     *
     * @return translated java boolean value from xml
     */
    public static boolean parseBoolean(final Optional<String> firstMaybeValue, final Optional<String> secondMaybeValue, final boolean defaultValue) {
        return firstMaybeValue.map(XmlUtil::parseBoolean).
            orElse(secondMaybeValue.map(XmlUtil::parseBoolean).
                orElse(defaultValue));
    }
}
