package com.inhochoi.springakka.persistence;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.inhochoi.springakka.core.ActorConfiguration;
import com.inhochoi.springakka.core.ActorProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static akka.pattern.Patterns.ask;

@Slf4j
@Import(ActorConfiguration.class)
@SpringBootApplication
public class PersistenceActorRunner implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(PersistenceActorRunner.class, args);
    }

    @Autowired
    private ActorProps actorProps;

    @Autowired
    private ActorSystem actorSystem;

    @Override
    public void run(String... args) {
        Props persistenceActorProps = actorProps.props(PersistenceActor.class, "persistence1");
        ActorRef persistenceActor = actorSystem.actorOf(persistenceActorProps, "PersistenceActor");
        ask(persistenceActor, "Get", Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (Long) it)
                .thenAccept(it -> log.info("PersistenceActor Actor Response :  {}", it));
    }
}
