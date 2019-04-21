package com.inhochoi.springakka.task;

import akka.actor.ActorRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static akka.pattern.Patterns.ask;

@Controller
@RequestMapping("/tasks/")
public class TaskController {
    @Autowired
    private ActorRef taskManagerActor;

    @ResponseBody
    @RequestMapping
    public Map getTasks() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> objectCompletableFuture = ask(taskManagerActor, new TaskManagerActor.GetTask(), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture();
        return (Map) objectCompletableFuture.get();
    }

    @ResponseBody
    @RequestMapping("/create")
    public String createTask(@RequestParam String taskId) throws ExecutionException, InterruptedException {
        return (String) ask(taskManagerActor, TaskManagerActor.CreateTask.create(taskId), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture()
                .get();
    }

    @ResponseBody
    @RequestMapping("/start")
    public String startTask(@RequestParam String taskId) throws ExecutionException, InterruptedException {
        return (String) ask(taskManagerActor, TaskManagerActor.StartTask.create(taskId), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture()
                .get();
    }

    @ResponseBody
    @RequestMapping("/stop")
    public String stopTask(@RequestParam String taskId) throws ExecutionException, InterruptedException {
        return (String) ask(taskManagerActor, TaskManagerActor.StopTask.create(taskId), Duration.of(1, ChronoUnit.SECONDS))
                .toCompletableFuture()
                .get();
    }
}
