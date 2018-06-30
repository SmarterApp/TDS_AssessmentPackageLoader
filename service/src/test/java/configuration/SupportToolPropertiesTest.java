package configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tds.support.tool.configuration.SupportToolProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class SupportToolPropertiesTest {
    private SupportToolProperties supportToolProperties;

    @Before
    public void setup() {
        supportToolProperties = new SupportToolProperties();
    }

    @Test(expected = IllegalArgumentException.class)
    public void itShouldNotAllowANullAssessmentServiceUrl() {
        supportToolProperties.setAssessmentUrl(null);
    }

    @Test
    public void itShouldReturnOptionalEmptyWhenArtRestApiUrlIsNull() {
        supportToolProperties.setArtRestUrl(null);

        assertThat(supportToolProperties.getArtRestUrl()).isEmpty();
    }

    @Test
    public void itShouldReturnOptionalEmptyWhenThssApiUrlIsNull() {
        supportToolProperties.setThssApiUrl(null);

        assertThat(supportToolProperties.getThssApiUrl()).isEmpty();
    }

    @Test
    public void itShouldReturnOptionalEmptyWhenTisApiUrlIsNull() {
        supportToolProperties.setTisApiUrl(null);

        assertThat(supportToolProperties.getTisApiUrl()).isEmpty();
    }

    @Test
    public void itShouldRemoveTrailingSlashFromUrl() {
        supportToolProperties.setAssessmentUrl("http://some-assessment-url/assessments/");

        assertThat(supportToolProperties.getAssessmentUrl()).isEqualTo("http://some-assessment-url/assessments");
    }

    @Test
    public void itShouldReturnDefaultSubjectsWhenEmptyListProvided() {
        supportToolProperties.setSubjects(Collections.emptyList());
        assertThat(supportToolProperties.getSubjects()).isEqualTo(new ArrayList<>(Arrays.asList("ELA", "MATH")));
    }

    @Test
    public void itShouldReturnDefaultSubjectsWhenNullProvided() {
        supportToolProperties.setSubjects(null);
        assertThat(supportToolProperties.getSubjects()).isEqualTo(new ArrayList<>(Arrays.asList("ELA", "MATH")));
    }

    @Test
    public void itShouldReturnProvidedSubjects() {
        List<String> subjects = new ArrayList<>(Arrays.asList("FOO", "BAZ"));
        supportToolProperties.setSubjects(subjects);
        assertThat(supportToolProperties.getSubjects()).isEqualTo(subjects);
    }
}
