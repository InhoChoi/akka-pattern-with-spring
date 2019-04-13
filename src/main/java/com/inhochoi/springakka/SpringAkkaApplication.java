package com.inhochoi.springakka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.inhochoi.springakka.child.ParentActor;
import com.inhochoi.springakka.core.ActorConfiguration;
import com.inhochoi.springakka.core.ActorProps;
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
    private ActorProps actorProps;

    @Autowired
    private ActorSystem actorSystem;

    @Override
    public void run(String... args) throws Exception {
        // 1. Hello Actor
        Props helloActorProps = actorProps.props(HelloActor.class, "inhochoi");
        ActorRef helloActor = actorSystem.actorOf(helloActorProps, "HelloActor");

        ask(helloActor, "Hello", Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (String) it)
                .thenAccept(it -> log.info("Hello Actor Response :  {}", it));

        // 2. Child Actor
        Props parentActorProps = actorProps.props(ParentActor.class);
        ActorRef parentActor = actorSystem.actorOf(parentActorProps, "ParentActor");
        ask(parentActor, new ParentActor.WorkStart(100L), Duration.of(1, ChronoUnit.SECONDS))
                .thenApply(it -> (Long) it)
                .thenAccept(it -> log.info("Parent Actor Response :  {}", it));
    }
}
