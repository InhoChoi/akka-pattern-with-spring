package com.inhochoi.springakka.task;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inhochoi.springakka.core.ActorProps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class TaskManagerActor extends AbstractPersistentActor {
    private List<String> taskIdList = Lists.newArrayList();
    private Map<String, ActorRef> idleTaskActorMap = Maps.newHashMap();
    private Map<String, ActorRef> runningTaskActorMap = Maps.newHashMap();

    @Autowired
    private ActorProps actorProps;

    @Override
    public Receive createReceiveRecover() {
        return ReceiveBuilder.create()
                .match(SnapshotOffer.class, it -> {
                    this.taskIdList = (List<String>) it.snapshot();

                    for (String taskId : taskIdList) {
                        ActorRef taskActor = context().actorOf(actorProps.props(TaskWorker.class, taskId), taskId);
                        idleTaskActorMap.put(taskId, taskActor);
                    }
                })
                .build();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetTask.class, it -> {
                    Map statusMap = Maps.newHashMap();
                    statusMap.put("all", taskIdList);
                    statusMap.put("idle", idleTaskActorMap.keySet());
                    statusMap.put("running", runningTaskActorMap.keySet());

                    sender().tell(statusMap, self());
                })
                .match(CreateTask.class, it -> {
                    if (taskIdList.contains(it.getTaskId())) {
                        sender().tell(it.getTaskId() + " is already created", self());
                        return;
                    }

                    taskIdList.add(it.getTaskId());
                    sender().tell(it.getTaskId() + " is created", self());

                    ActorRef taskActor = context().actorOf(actorProps.props(TaskWorker.class, it.getTaskId()), it.getTaskId());
                    idleTaskActorMap.put(it.getTaskId(), taskActor);

                    saveSnapshot(taskIdList);
                })
                .match(StartTask.class, it -> {
                    if (runningTaskActorMap.containsKey(it.getTaskId())) {
                        sender().tell(it.getTaskId() + " is already started", self());
                        return;
                    }

                    if (idleTaskActorMap.containsKey(it.getTaskId())) {
                        idleTaskActorMap.get(it.getTaskId()).tell(TaskWorker.StartWork.create(), self());
                        runningTaskActorMap.put(it.getTaskId(), idleTaskActorMap.get(it.getTaskId()));
                        idleTaskActorMap.remove(it.getTaskId());
                        sender().tell(it.getTaskId() + " is started", self());
                        return;
                    }

                    sender().tell(it.getTaskId() + " is not existed", self());
                })
                .match(StopTask.class, it -> {
                    if (runningTaskActorMap.containsKey(it.getTaskId())) {
                        runningTaskActorMap.get(it.getTaskId()).tell(TaskWorker.StopWork.create(), self());
                        idleTaskActorMap.put(it.getTaskId(), runningTaskActorMap.get(it.getTaskId()));
                        runningTaskActorMap.remove(it.getTaskId());

                        sender().tell(it.getTaskId() + " is stopping", self());
                        return;
                    }

                    if (idleTaskActorMap.containsKey(it.getTaskId())) {
                        sender().tell(it.getTaskId() + " is not running", self());
                        return;
                    }

                    sender().tell(it.getTaskId() + " is not existed", self());
                })
                .build();
    }

    @Override
    public String persistenceId() {
        return "taskManagerActor";
    }

    public static class GetTask {
    }

    @Getter
    @AllArgsConstructor(staticName = "create")
    public static class CreateTask {
        private String taskId;
    }

    @Getter
    @AllArgsConstructor(staticName = "create")
    public static class StartTask {
        private String taskId;
    }

    @Getter
    @AllArgsConstructor(staticName = "create")
    public static class StopTask {
        private String taskId;
    }
}
