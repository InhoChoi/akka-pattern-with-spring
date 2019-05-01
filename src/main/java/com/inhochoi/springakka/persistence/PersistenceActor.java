package com.inhochoi.springakka.persistence;

import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class PersistenceActor extends AbstractPersistentActor {
    private State state = State.create(0L);
    private String name;

    public PersistenceActor(String name) {
        this.name = name;
    }

    @Override
    public Receive createReceiveRecover() {
        return ReceiveBuilder.create()
                .match(SnapshotOffer.class, this::recover)
                .build();
    }

    private void recover(SnapshotOffer snapshotOffer) {
        this.state = (State) snapshotOffer.snapshot();
    }


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .matchEquals("Get", this::get)
                .build();
    }

    private void get(String get) {
        sender().tell(state.getCount(), self());
        state.increase();
        saveSnapshot(state);
    }


    @Override
    public String persistenceId() {
        return name;
    }

    @Getter
    @AllArgsConstructor(staticName = "create")
    public static class State {
        @JsonProperty("count")
        private Long count;

        public void increase() {
            count = count++;
        }
    }
}
