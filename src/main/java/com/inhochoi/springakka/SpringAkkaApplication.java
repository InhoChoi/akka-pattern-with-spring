package com.inhochoi.springakka;

import akka.actor.ActorRef;
import com.inhochoi.springakka.child.ParentActor;
import com.inhochoi.springakka.core.ActorConfiguration;
import com.inhochoi.springakka.core.ActorFactory;
import com.inhochoi.springakka.hello.HelloActor;
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
public class SpringAkkaApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringAkkaApplication.class, args);
    }

    @Autowired
    private ActorFactory actorFactory;

    @Override
    public void run(String... args) throws Exception {
        // 1. Hello Actor
        ActorRef helloActor = actorFactory.actorOf(HelloActor.class, "HelloActor", "inhochoi");
        ask(helloActor, "Hello", Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (String) it)
                .thenAccept(it -> log.info("Hello Actor Response :  {}", it));

        // 2. Child Actor
        ActorRef parentActor = actorFactory.actorOf(ParentActor.class, "ParentActor");
        ask(parentActor, new ParentActor.WorkStart(100L), Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (Long) it)
                .thenAccept(it -> log.info("Parent Actor Response :  {}", it));
    }
}
