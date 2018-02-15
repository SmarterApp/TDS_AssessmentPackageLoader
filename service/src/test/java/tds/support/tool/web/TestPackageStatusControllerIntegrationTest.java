package tds.support.tool.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import tds.common.configuration.SecurityConfiguration;
import tds.common.web.advice.ExceptionAdvice;
import tds.support.job.JobType;
import tds.support.job.Status;
import tds.support.job.TargetSystem;
import tds.support.job.TestPackageStatus;
import tds.support.job.TestPackageTargetSystemStatus;
import tds.support.tool.services.TestPackageStatusService;

import static java.lang.Math.toIntExact;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestPackageStatusController.class)
@Import({ExceptionAdvice.class, SecurityConfiguration.class})
public class TestPackageStatusControllerIntegrationTest {
    private MockMvc http;

    @Autowired
    private ObjectMapper objectMapper;

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
                JobType.LOADER,
                Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("second test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )),
            new TestPackageStatus("third test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        final PageRequest pageRequest = new PageRequest(0, 3);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(testPackageStatuses, pageRequest, 10);

        when(mockTestPackageStatusService.getAll(pageRequest)).thenReturn(testPackageStatusPage);

        http.perform(get("/api/load/status")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnPageInformationInRespoonse() throws Exception {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("first test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("second test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )),
            new TestPackageStatus("third test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        final PageRequest pageRequest = new PageRequest(0, 3);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(testPackageStatuses, pageRequest, 10);

        when(mockTestPackageStatusService.getAll(pageRequest)).thenReturn(testPackageStatusPage);

        http.perform(get("/api/load/status")
            .param("page", Integer.toString(pageRequest.getPageNumber()))
            .param("size", Integer.toString(pageRequest.getPageSize()))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number", is(pageRequest.getPageNumber())))
            .andExpect(jsonPath("$.size", is(pageRequest.getPageSize())))
            .andExpect(jsonPath("$.first", is(true)))
            .andExpect(jsonPath("$.last", is(false)))
            .andExpect(jsonPath("$.totalElements", is(toIntExact(testPackageStatusPage.getTotalElements()))))
            .andExpect(jsonPath("$.numberOfElements", is(testPackageStatusPage.getNumberOfElements())))
            .andExpect(jsonPath("$.totalPages", is(4)))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    public void shouldReturnSortInformationInResponse() throws Exception {
        final List<TestPackageStatus> testPackageStatuses = Arrays.asList(
            new TestPackageStatus("first test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Collections.singletonList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS)
            )),
            new TestPackageStatus("second test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )),
            new TestPackageStatus("third test package",
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                JobType.LOADER,
                Arrays.asList(
                new TestPackageTargetSystemStatus(TargetSystem.TDS, Status.SUCCESS),
                new TestPackageTargetSystemStatus(TargetSystem.ART, Status.SUCCESS)
            )));

        final Sort sort = new Sort(Sort.Direction.DESC, "uploadedAt");
        final PageRequest pageRequest = new PageRequest(0, 3, sort);
        final Page<TestPackageStatus> testPackageStatusPage = new PageImpl<>(testPackageStatuses, pageRequest, 10);

        when(mockTestPackageStatusService.getAll(pageRequest)).thenReturn(testPackageStatusPage);

        http.perform(get("/api/load/status")
            .param("page", Integer.toString(pageRequest.getPageNumber()))
            .param("size", Integer.toString(pageRequest.getPageSize()))
            .param("sort", "uploadedAt,DESC")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sort[*].direction").value("DESC"))
            .andExpect(jsonPath("$.sort[*].property").value("uploadedAt"));
    }
}
