package esprit.tn.traningmsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TraningMsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraningMsProjectApplication.class, args);
    }

}
