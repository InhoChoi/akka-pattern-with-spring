package com.inhochoi.springakka.child;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ChildActor extends AbstractActor {
    @Autowired
    private ChildService childService;

    private Long number;

    public ChildActor(Long number) {
        this.number = number;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(ParentActor.Request.class, this::receive)
                .build();
    }

    private void receive(ParentActor.Request request) {
        log.info("Child Actor : {}, Actor got message, {}", getSelf().path(), request);
        sender().tell(childService.multipy(request.getInput(), number), self());
    }

    @Getter
    @Builder
    public static class Response {
        private Long output;
    }
}
