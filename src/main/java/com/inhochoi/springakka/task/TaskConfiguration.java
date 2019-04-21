package com.inhochoi.springakka.task;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.inhochoi.springakka.core.ActorConfiguration;
import com.inhochoi.springakka.core.ActorProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ActorConfiguration.class)
public class TaskConfiguration {
    @Autowired
    private ActorProps actorProps;

    @Autowired
    private ActorSystem actorSystem;

    @Bean
    public ActorRef taskManagerActor() {
        Props props = actorProps.props(TaskManagerActor.class);
        return actorSystem.actorOf(props, "taskManagerActor");
    }
}
