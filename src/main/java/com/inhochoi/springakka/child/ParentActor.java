package com.inhochoi.springakka.child;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.inhochoi.springakka.core.ActorProps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;

public class ParentActor extends AbstractActor {
    @Autowired
    private ActorProps actorProps;

    private ActorRef actor1;
    private ActorRef actor2;
    private ActorRef actor3;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        Props actor1 = actorProps.props(ChildActor.class, 1L);
        Props actor2 = actorProps.props(ChildActor.class, 2L);
        Props actor3 = actorProps.props(ChildActor.class, 3L);

        this.actor1 = context().actorOf(actor1, "Child1");
        this.actor2 = context().actorOf(actor2, "Child2");
        this.actor3 = context().actorOf(actor3, "Child3");
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
    @ToString
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
