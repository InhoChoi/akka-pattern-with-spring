package com.inhochoi.springakka.stream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.Timeout;
import com.google.common.base.Stopwatch;
import com.inhochoi.springakka.core.ActorConfiguration;
import com.inhochoi.springakka.core.ActorProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import scala.compat.java8.FutureConverters;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@Slf4j
@Import(ActorConfiguration.class)
@SpringBootApplication
public class StreamRunner implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(StreamRunner.class, args);
    }

    @Autowired
    private ActorProps actorProps;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        Props streamActorProps = actorProps.props(StreamActor.class);

        ActorRef router = actorSystem.actorOf(new RoundRobinPool(100).props(streamActorProps).
                        withDispatcher("blocking-io-dispatcher"),
                "streamActor");

        Materializer materializer = ActorMaterializer.create(actorSystem);
        Timeout timeout = Timeout.apply(1, TimeUnit.SECONDS);


        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        CompletionStage<List<String>> listCompletionStage = Source.range(1, 100)
                .map(String::valueOf)
                .ask(100, router, String.class, timeout)
                .runWith(Sink.seq(), materializer);

        List<String> strings = listCompletionStage.toCompletableFuture().get();
        stopwatch.stop();

        log.info("Time : {}", stopwatch.toString());
        for (String str : strings) {
            log.info("Result : {}", str);
        }

        // Shutdown ActorSystem, Spring Application Context
        FutureConverters.toJava(actorSystem.terminate()).toCompletableFuture().get();
        System.exit(SpringApplication
                .exit(applicationContext));
    }
}
