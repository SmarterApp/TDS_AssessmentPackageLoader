package tds.support.tool.utils;

import tds.testpackage.model.TestPackage;

public class TestPackageUtils {
    public static String getAssessmentKey(final TestPackage testPackage, final String assessmentId) {
        return String.format("(%s)%s-%s", testPackage.getPublisher(), assessmentId, testPackage.getAcademicYear());
    }
}
