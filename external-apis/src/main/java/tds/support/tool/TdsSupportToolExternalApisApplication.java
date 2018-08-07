package tds.support.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;
import tds.support.tool.configuration.EmbeddedTomcatConfiguration;
import tds.support.tool.configuration.FiltersConfiguration;

@SpringBootApplication
@RestController
public class TdsSupportToolExternalApisApplication {

    public static void main(String[] args) {
        System.out.println("Version 3");
        SpringApplication.run(TdsSupportToolExternalApisApplication.class, args);
    }
}
