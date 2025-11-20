package org.nishgrid.clienterp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NishgridClientErpBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NishgridClientErpBackendApplication.class, args);
    }
}




