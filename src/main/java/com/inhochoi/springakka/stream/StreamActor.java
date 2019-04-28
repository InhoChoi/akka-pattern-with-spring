package com.inhochoi.springakka.stream;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class StreamActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(String.class, it -> {
                    Thread.sleep(100);
                    sender().tell(it + " with " + self().path(), sender());
                })
                .build();
    }
}
