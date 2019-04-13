package com.inhochoi.springakka.core;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import lombok.SneakyThrows;
import org.omg.SendingContext.RunTimeOperations;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
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
    public Actor produce() {
        Class[] parameterTypes = Arrays.stream(arguments)
                .map(Object::getClass)
                .toArray(Class[]::new);

        final Actor actor;
        try {
            actor = actorClass.getDeclaredConstructor(parameterTypes).newInstance(arguments);
        } catch (Exception e) {
            final String errorMessage = String.format("Actor %s don't have constructor (%s)", actorClass.getName(), Arrays.toString(parameterTypes));
            throw new IllegalArgumentException(errorMessage, e);
        }
        applicationContext.getAutowireCapableBeanFactory().autowireBean(actor);
        return actor;
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return actorClass;
    }
}
