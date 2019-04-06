package com.inhochoi.springakka.core;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import static com.inhochoi.springakka.core.SpringExtension.SPRING_EXTENSION_PROVIDER;

public class ActorFactory {
    private ActorSystem actorSystem;

    public ActorFactory(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public ActorRef actorOf(Class<? extends Actor> actorClass, Object... arguments) {
        return actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem)
                .props(actorClass, arguments));
    }

    public ActorRef actorOf(ActorContext actorContext, Class<? extends Actor> actorClass, Object... arguments) {
        return actorContext.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem)
                .props(actorClass, arguments));
    }
}
