package tds.support.tool.services.converter;

import tds.testpackage.legacy.model.Testspecification;
import tds.testpackage.model.TestPackage;

import java.util.List;

public interface TestPackageConverterService {
    TestPackage convertToNew(final String testPackageName, final List<Testspecification> testspecification);
}
