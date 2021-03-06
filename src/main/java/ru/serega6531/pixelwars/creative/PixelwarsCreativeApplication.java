package ru.serega6531.pixelwars.creative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@EnableTransactionManagement
@SpringBootApplication
public class PixelwarsCreativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixelwarsCreativeApplication.class, args);
    }

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(this.getClass());
    }

}
