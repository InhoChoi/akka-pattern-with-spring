package com.inhochoi.springakka.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Slf4j
@Import(TaskConfiguration.class)
@SpringBootApplication
public class TaskRunner implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TaskRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
