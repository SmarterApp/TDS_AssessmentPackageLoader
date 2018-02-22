package tds.support.tool.services;

import tds.teacherhandscoring.model.TeacherHandScoringApiResult;
import tds.testpackage.model.TestPackage;

import java.io.IOException;

public interface THSSService {
    /**
     * Sends the THSS configuration portion of the Test Specification Package to THSS
     *
     * @param testPackage test package that contains THSS configuration data
     * @return Response (success/failure and error message) from THSS
     * @throws IOException
     */
    TeacherHandScoringApiResult loadTestPackage(final TestPackage testPackage) throws IOException;
}
