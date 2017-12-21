package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import tds.support.job.Job;
import tds.support.job.JobType;
import tds.support.tool.services.JobService;

@RestController
@RequestMapping("/api/load")
public class TestPackageController {
    private final JobService jobService;

    @Autowired
    public TestPackageController(final JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Starts the test package job
     *
     * @param file the test package
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> loadPackage(@RequestParam("file") MultipartFile file,
                                           @RequestParam("skipArt") final boolean skipArt,
                                           @RequestParam("skipScoring") final boolean skipScoring) throws IOException {
        Job job = jobService.startPackageImport(file.getOriginalFilename(), file.getInputStream(), file.getSize(), skipArt, skipScoring);
        return ResponseEntity.ok(job);
    }

    /**
     * Gets the test package jobs
     *
     * @param jobTypes the job types to fetch
     * @return {@link org.springframework.http.ResponseEntity} containing the new {@link tds.support.job.Job}
     * @throws IOException thrown if there is an issue with accessing the file
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobs(@RequestParam(value = "jobType", required = false) JobType... jobTypes) throws IOException {
        return ResponseEntity.ok(jobService.findJobs(jobTypes));
    }
}
