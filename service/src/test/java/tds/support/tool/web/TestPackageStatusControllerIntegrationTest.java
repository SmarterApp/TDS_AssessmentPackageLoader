package tds.support.tool.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.services.TestPackageStatusService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestPackageStatusController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class TestPackageStatusControllerIntegrationTest {
    @Autowired
    private MockMvc http;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TestPackageStatusService mockTestPackageStatusService;

    @Test
    public void shouldFindAllTestPackageStatusRecords() throws Exception {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("test package; TDS Only", LocalDateTime.now(), Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("test package: TDS and ART", LocalDateTime.now(), Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        when(mockTestPackageStatusService.getAll()).thenReturn(testPackageStatuses);

        final MvcResult result = http.perform(get("/api/load/status")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andReturn();

        final List<TestPackageStatus> statusesInResponse =
            objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TestPackageStatus.class));

        assertThat(statusesInResponse).hasSize(2);
    }

    @Test
    public void shouldReturnSuccessAndEmptyListWhenThereAreNoTestPackageStatusRecords() throws Exception {
        when(mockTestPackageStatusService.getAll()).thenReturn(Collections.emptyList());

        final MvcResult result = http.perform(get("/api/load/status")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andReturn();

        final List<TestPackageStatus> statusesInResponse =
            objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TestPackageStatus.class));

        assertThat(statusesInResponse).hasSize(0);
    }
}
