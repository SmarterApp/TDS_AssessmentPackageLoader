package tds.support.tool.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * Get a collection of all {@link tds.support.job.TestPackageStatus}es to describe the state of each
     * {@link tds.testpackage.model.TestPackage} managed by the Support Tool.
     *
     * @return A collection of all {@link tds.support.job.TestPackageStatus}es
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestPackageStatus>> getAll() {
        return ResponseEntity.ok(testPackageStatusService.getAll());
    }
}
