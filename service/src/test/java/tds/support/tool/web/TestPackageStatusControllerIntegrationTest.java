package tds.support.tool.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.*;
import tds.support.tool.services.TestPackageStatusService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestPackageStatusController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class TestPackageStatusControllerIntegrationTest {
    private MockMvc http;

    @MockBean
    private TestPackageStatusService mockTestPackageStatusService;

    @Before
    public void setup() {
        http = MockMvcBuilders.standaloneSetup(new TestPackageStatusController(mockTestPackageStatusService))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(new SortHandlerMethodArgumentResolver()))
            .build();
    }

    @Test
    public void shouldAcceptAPageableParameterAndReturn200() throws Exception {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("first test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("second test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )),
            new TestPackageStatus("third test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOAD,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        when(mockTestPackageStatusService.getAll()).thenReturn(testPackageStatuses);

        http.perform(get("/api/load/status")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

}
