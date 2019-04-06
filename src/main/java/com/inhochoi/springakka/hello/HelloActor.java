package com.inhochoi.springakka.hello;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class HelloActor extends AbstractActor {
    @Autowired
    private HelloService helloService;

    private String name;

    public HelloActor(String name) {
        this.name = name;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log.info("Start Hello Actor");
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log.info("Stop Hello Actor");
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(String.class, this::hello)
                .build();
    }

    private void hello(String message) {
        String helloWorld = helloService.getHelloWorld(name);
        sender().tell(helloWorld, self());
    }
}
