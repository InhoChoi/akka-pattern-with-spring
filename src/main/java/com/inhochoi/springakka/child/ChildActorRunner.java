package com.inhochoi.springakka.child;

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
public class ChildActorRunner implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ChildActorRunner.class, args);
    }

    @Autowired
    private ActorProps actorProps;

    @Autowired
    private ActorSystem actorSystem;

    @Override
    public void run(String... args) {
        Props parentActorProps = actorProps.props(ParentActor.class);
        ActorRef parentActor = actorSystem.actorOf(parentActorProps, "ParentActor");
        ask(parentActor, new ParentActor.WorkStart(100L), Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (Long) it)
                .thenAccept(it -> log.info("Parent Actor Response :  {}", it));
    }
}
