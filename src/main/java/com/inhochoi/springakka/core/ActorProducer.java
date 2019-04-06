package com.inhochoi.springakka.core;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

public class ActorProducer implements IndirectActorProducer {
    private ApplicationContext applicationContext;
    private Class<? extends Actor> actorClass;
    private Object[] arguments;

    public ActorProducer(ApplicationContext applicationContext,
                         Class<? extends Actor> actorClass, Object... arguments) {
        this.applicationContext = applicationContext;
        this.actorClass = actorClass;
        this.arguments = arguments;
    }


    @Override
    @SneakyThrows
    public Actor produce() {
        Class[] parameterTypes = Arrays.stream(arguments)
                .map(Object::getClass)
                .toArray(Class[]::new);

        final Actor actor = actorClass.getDeclaredConstructor(parameterTypes).newInstance(arguments);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(actor);
        return actor;
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return actorClass;
    }
}
