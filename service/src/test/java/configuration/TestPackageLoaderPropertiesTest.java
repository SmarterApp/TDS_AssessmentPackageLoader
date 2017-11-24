package configuration;

import org.junit.Before;
import org.junit.Test;

import tds.support.tool.configuration.TestPackageLoaderProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPackageLoaderPropertiesTest {
    private TestPackageLoaderProperties testPackageLoaderProperties;

    @Before
    public void setup() {
        testPackageLoaderProperties = new TestPackageLoaderProperties();
    }

    @Test(expected = IllegalArgumentException.class)
    public void itShouldNotAllowANullAssessmentServiceUrl() {
        testPackageLoaderProperties.setAssessmentUrl(null);
    }

    @Test
    public void itShouldReturnOptionalEmptyWhenArtRestApiUrlIsNull() {
        testPackageLoaderProperties.setArtRestUrl(null);

        assertThat(testPackageLoaderProperties.getArtRestUrl()).isEmpty();
    }

    @Test
    public void itShouldReturnOptionalEmptyWhenThssApiUrlIsNull() {
        testPackageLoaderProperties.setThssApiUrl(null);

        assertThat(testPackageLoaderProperties.getThssApiUrl()).isEmpty();
    }

    @Test
    public void itShouldReturnOptionalEmptyWhenTisApiUrlIsNull() {
        testPackageLoaderProperties.setTisApiUrl(null);

        assertThat(testPackageLoaderProperties.getTisApiUrl()).isEmpty();
    }

    @Test
    public void itShouldRemoveTrailingSlashFromUrl() {
        testPackageLoaderProperties.setAssessmentUrl("http://some-assessment-url/assessments/");

        assertThat(testPackageLoaderProperties.getAssessmentUrl()).isEqualTo("http://some-assessment-url/assessments");
    }
}
