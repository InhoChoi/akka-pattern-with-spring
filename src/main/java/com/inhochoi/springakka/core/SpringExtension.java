package com.inhochoi.springakka.core;

import akka.actor.*;
import org.springframework.context.ApplicationContext;

public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {
    public static final SpringExtension SPRING_EXTENSION_PROVIDER
            = new SpringExtension();

    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    public static class SpringExt implements Extension {
        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public Props props(Class<? extends Actor> actorClass, Object... args) {
            return Props.create(
                    ActorProducer.class, applicationContext, actorClass, args);
        }
    }
}
