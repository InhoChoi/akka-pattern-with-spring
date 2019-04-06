package com.inhochoi.springakka.child;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.inhochoi.springakka.core.ActorFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;

public class ParentActor extends AbstractActor {
    @Autowired
    private ActorFactory actorFactory;


    private ActorRef actor1;
    private ActorRef actor2;
    private ActorRef actor3;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.actor1 = actorFactory.actorOf(context(), ChildActor.class, 1L);
        this.actor2 = actorFactory.actorOf(context(), ChildActor.class, 2L);
        this.actor3 = actorFactory.actorOf(context(), ChildActor.class, 3L);
    }


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(WorkStart.class, this::start)
                .build();
    }

    @SneakyThrows
    private void start(WorkStart workStart) {
        CompletableFuture<Long> ask1 = ask(actor1, new Request(workStart.getInput()), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture()
                .thenApply(it -> (Long) it);
        CompletableFuture<Long> ask2 = ask(actor2, new Request(workStart.getInput()), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture()
                .thenApply(it -> (Long) it);
        CompletableFuture<Long> ask3 = ask(actor3, new Request(workStart.getInput()), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture()
                .thenApply(it -> (Long) it);

        CompletableFuture.allOf(ask1, ask2, ask3)
                .join();

        sender().tell(ask1.get() + ask2.get() + ask3.get(), self());
    }

    @Getter
    @AllArgsConstructor
    static class Request {
        private Long input;
    }

    @Getter
    @AllArgsConstructor
    public static class WorkStart {
        private Long input;
    }

    @Getter
    @AllArgsConstructor
    static class WorkResult {
        private String result;
    }
}
