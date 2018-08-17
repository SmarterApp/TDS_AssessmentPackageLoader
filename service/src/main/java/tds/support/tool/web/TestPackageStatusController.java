package tds.support.tool.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import tds.support.job.TestPackageStatus;
import tds.support.tool.services.TestPackageStatusService;

@RestController
@RequestMapping("api/load/status")
public class TestPackageStatusController {
    private final TestPackageStatusService testPackageStatusService;

    public TestPackageStatusController(final TestPackageStatusService testPackageStatusService) {
        this.testPackageStatusService = testPackageStatusService;
    }

    /**
     * Gets the test package statuses.
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the {@link tds.support.job.TestPackageStatus}es
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestPackageStatus>> getStatuses() {
        return ResponseEntity.ok(testPackageStatusService.getAll());
    }
}
