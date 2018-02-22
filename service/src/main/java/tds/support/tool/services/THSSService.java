package tds.support.tool.services;

import tds.common.ValidationError;
import tds.testpackage.model.TestPackage;

import java.io.IOException;
import java.util.Optional;

public interface THSSService {
    /**
     * Sends the THSS configuration portion of the Test Specification Package to THSS
     *
     * @param testPackage test package that contains THSS configuration data
     * @return Response (success/failure and error message) from THSS
     * @throws IOException
     */
    Optional<ValidationError> loadTestPackage(final String name, final TestPackage testPackage) throws IOException;

    Optional<ValidationError> deleteTestPackage(final TestPackage testPackage);
}
