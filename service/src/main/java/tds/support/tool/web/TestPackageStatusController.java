package tds.support.tool.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @param pageable A {@link org.springframework.data.domain.Pageable} containing the paging/sorting information
     *                 for the request
     * @return A {@link org.springframework.data.domain.Page} of {@link tds.support.job.TestPackageStatus}es.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TestPackageStatus>> getAllByPage(final Pageable pageable) {
        return ResponseEntity.ok(testPackageStatusService.getAll(pageable));
    }

    /**
     * Search for all {@link tds.support.job.TestPackageStatus}es whose name contains the specified search term and
     * return a page of results.
     *
     * @param searchTerm The part of the {@link tds.testpackage.model.TestPackage} name to search for
     * @param pageable A {@link org.springframework.data.domain.Pageable} containing the paging/sorting information
     *                 for the request
     * @return A {@link org.springframework.data.domain.Page} of {@link tds.support.job.TestPackageStatus}es that
     * have a name containing the search term text.
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TestPackageStatus>> searchByName(@RequestParam final String searchTerm,
                                                                final Pageable pageable) {
        return ResponseEntity.ok(testPackageStatusService.searchByName(searchTerm, pageable));
    }
}
