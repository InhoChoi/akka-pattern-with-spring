package com.inhochoi.springakka.core;

import akka.actor.*;
import org.springframework.context.ApplicationContext;

public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {
    static final SpringExtension SPRING_EXTENSION_PROVIDER
            = new SpringExtension();

    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    static class SpringExt implements Extension {
        private volatile ApplicationContext applicationContext;

        void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        Props props(Class<? extends Actor> actorClass, Object... args) {
            return Props.create(
                    ActorProducer.class, applicationContext, actorClass, args);
        }
    }
}
