package tds.support.tool;

import com.google.common.io.ByteStreams;
import org.apache.http.entity.ContentType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@SpringBootApplication
@RestController
public class TdsSupportToolApplication {

    // Map all urls that do not have an extension (ie. image.png)
    // to the angular app entry point (index.html)
    //
    // With SAML enabled, using the standard spring forwarding mechanism was not functioning.
    @GetMapping(value = "/**/{path:[^\\.]*}")
    public void redirect(HttpServletResponse response) throws Exception {
        response.setContentType(ContentType.TEXT_HTML.getMimeType());
        final InputStream inputStream = new ClassPathResource("/static/index.html").getInputStream();
        ByteStreams.copy(inputStream, response.getOutputStream());
    }

    public static void main(String[] args) {
        SpringApplication.run(TdsSupportToolApplication.class, args);
    }
}
