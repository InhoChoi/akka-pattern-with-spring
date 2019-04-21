package com.inhochoi.springakka.task;

import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import akka.stream.ActorMaterializer;
import akka.stream.KillSwitches;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
public class TaskWorker extends AbstractPersistentActor {
    private String taskId;

    private Long offset = 0L;
    private UniqueKillSwitch killSwitch = null;

    public TaskWorker(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        log.info("Task Id : {}, Start : {}, Offset : {}", taskId, getSelf().path(), offset);
    }

    @Override
    public void postStop() {
        super.postStop();

        log.info("Task Id : {}, Stop : {}, Offset : {}", taskId, getSelf().path(), offset);
    }

    @Override
    public Receive createReceiveRecover() {
        return ReceiveBuilder.create()
                .match(SnapshotOffer.class, it -> this.offset = (Long) it.snapshot())
                .build();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StartWork.class, it -> {
                    log.info("Start task id : {}", taskId);


                    Materializer materializer = ActorMaterializer.create(context());

                    this.killSwitch = Source.range(offset.intValue(), 100_000)
                            .throttle(1, Duration.of(2, ChronoUnit.SECONDS))
                            .viaMat(KillSwitches.single(), Keep.right())
                            .to(Sink.foreach(number -> {
                                log.info("Task Id : {}, Int : {}", taskId, number);
                                offset++;
                            }))
                            .run(materializer);

                })
                .match(StopWork.class, it -> {
                    log.info("Stop task id : {}", taskId);
                    killSwitch.shutdown();
                    saveSnapshot(offset);
                })
                .build();
    }

    @Override
    public String persistenceId() {
        return taskId;
    }


    @AllArgsConstructor(staticName = "create")
    public static class StartWork {
    }

    @AllArgsConstructor(staticName = "create")
    public static class StopWork {
    }
}
