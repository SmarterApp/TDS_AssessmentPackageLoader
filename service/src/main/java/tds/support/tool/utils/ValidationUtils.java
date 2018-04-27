package tds.support.tool.utils;

public class ValidationUtils {
    public static boolean isNotNullAndHasMaxLength(final String value, final int length) {
        return value != null && value.length() <= length;
    }
}
