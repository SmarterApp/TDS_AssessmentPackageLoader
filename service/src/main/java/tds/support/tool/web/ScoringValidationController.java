package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.tool.services.TestPackageJobService;
import tds.support.tool.services.TestResultsJobService;
import tds.support.tool.services.scoring.TestResultsService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scoring")
public class ScoringValidationController {
    private final TestResultsJobService testResultsJobService;

    @Autowired
    public ScoringValidationController(final TestResultsJobService testResultsJobService) {
        this.testResultsJobService = testResultsJobService;
    }

    /**
     * Starts the test package job
     *
     * @param file the test package
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> loadPackage(@RequestParam("file") final MultipartFile file) throws IOException {
        Job job = testResultsJobService.startTestResultsImport(file.getOriginalFilename(), file.getInputStream(), file.getSize());
        return ResponseEntity.ok(job);
    }

    /**
     * Gets the scoring jobs
     *
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs() {
        return ResponseEntity.ok(testResultsJobService.findJobs());
    }
}