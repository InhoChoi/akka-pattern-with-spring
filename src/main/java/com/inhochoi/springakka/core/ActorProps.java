package com.inhochoi.springakka.core;

import akka.actor.*;

import static com.inhochoi.springakka.core.SpringExtension.SPRING_EXTENSION_PROVIDER;

public class ActorProps {
    private ActorSystem actorSystem;

    public ActorProps(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public Props props(Class<? extends Actor> actorClass, Object... arguments) {
        return SPRING_EXTENSION_PROVIDER.get(actorSystem)
                .props(actorClass, arguments);
    }
}
