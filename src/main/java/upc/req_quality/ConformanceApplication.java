package upc.req_quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"upc.req_quality"})
public class ConformanceApplication {
    public static void main(String[] args) {
       SpringApplication.run(ConformanceApplication.class, args);
    }
}
