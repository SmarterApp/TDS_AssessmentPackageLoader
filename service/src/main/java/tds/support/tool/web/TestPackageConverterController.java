package tds.support.tool.web;

import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import tds.support.tool.services.converter.TestPackageConverterService;

import java.io.*;

@RestController
@RequestMapping("/api/convert")
public class TestPackageConverterController {
    private static final Logger log = LoggerFactory.getLogger(TestPackageConverterController.class);
    private final TestPackageConverterService testPackageConverterService;

    @Autowired
    public TestPackageConverterController(final TestPackageConverterService testPackageConverterService) {
        this.testPackageConverterService = testPackageConverterService;
    }

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> convertToNew(@RequestParam("file") MultipartFile file,
                                               @RequestParam String testPackageName) {
        String testPackageXml;

        try {
            testPackageXml= testPackageConverterService.extractAndConvertTestSpecifications(testPackageName, convert(file));
        } catch (IOException e) {
            final String error = String.format("An error occurred when attempting to unmarshall the test specifications " +
                    "included in the zip file for test package %s", testPackageName);
            log.error(error, e);
            return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return ResponseEntity.ok(testPackageXml);
    }

    private File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
