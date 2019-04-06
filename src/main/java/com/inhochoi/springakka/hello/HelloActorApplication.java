package com.inhochoi.springakka.hello;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.inhochoi.springakka.core.ActorConfiguration;
import com.inhochoi.springakka.core.ActorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;
import static com.inhochoi.springakka.core.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Slf4j
@Import(ActorConfiguration.class)
@SpringBootApplication
public class HelloActorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HelloActorApplication.class, args);
    }

    @Autowired
    private ActorFactory actorFactory;

    @Override
    public void run(String... args) {
        ActorRef helloActor = actorFactory.actorOf(HelloActor.class, "Inhochoi");

        ask(helloActor, "Hello", Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (String) it)
                .thenAccept(it -> log.info("Print {}", it));
    }
}
