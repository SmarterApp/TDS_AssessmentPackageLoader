package tds.support.tool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import tds.support.job.Job;
import tds.support.tool.services.JobService;

@RestController
@RequestMapping("/api/load")
public class TestPackageController {
    private final JobService jobService;

    @Autowired
    public TestPackageController(final JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> loadPackage(@RequestParam("file") MultipartFile file) throws IOException {
        Job job = jobService.startPackageImport(file.getOriginalFilename(), file.getInputStream(), file.getSize());
        return ResponseEntity.ok(job);
    }
}
