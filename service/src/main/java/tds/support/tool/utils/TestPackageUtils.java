package tds.support.tool.utils;

import tds.testpackage.legacy.model.Testspecification;
import tds.testpackage.model.TestPackage;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestPackageUtils {
    public static String getAssessmentKey(final TestPackage testPackage, final String assessmentId) {
        return String.format("(%s)%s-%s", testPackage.getPublisher(), assessmentId, testPackage.getAcademicYear());
    }

    public static String parseCohort(final String formKey) {
        // e.g., SBAC-OP-FIXED-G8E-Perf-BrainWorks-Spring-2015-2016:SleepDream-ENU - the cohort is "SleepDream"
        return formKey.substring(formKey.indexOf(":") + 1, formKey.lastIndexOf("-"));
    }

    public static String parseIdFromKey(final String key) {
        return key.substring(key.indexOf("-") + 1, key.length());
    }

    public static String parseItemGroupId(final String groupKey) {
        final String[] groupKeyStrSplit = groupKey.split("-");
        return groupKey.startsWith("I")
                ? groupKeyStrSplit[groupKeyStrSplit.length - 1]
                : groupKeyStrSplit[groupKeyStrSplit.length - 2];
    }

    public static boolean isAdministrationPackage(final Testspecification testSpecification) {
        return testSpecification.getPurpose().equalsIgnoreCase("administration") &&
                testSpecification.getAdministration() != null;
    }

    public static boolean isScoringPackage(final Testspecification testSpecification) {
        return testSpecification.getPurpose().equalsIgnoreCase("scoring") &&
                testSpecification.getScoring() != null;
    }

    public static String parseVersion(final BigDecimal version) {
        final String rawVersion = String.valueOf(version);
        return rawVersion.substring(0, rawVersion.indexOf(".")); // ignore the decimal
    }

    public static String formatDate(final String publishDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mma");
        return formatter.parse(publishDate).toInstant().toString();
    }
}
