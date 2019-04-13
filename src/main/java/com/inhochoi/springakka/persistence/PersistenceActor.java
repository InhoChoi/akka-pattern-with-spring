package com.inhochoi.springakka.persistence;

import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;

public class PersistenceActor extends AbstractPersistentActor {
    private Long count = 0L;

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
        this.count = (Long) snapshotOffer.snapshot();
    }


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .matchEquals("Get", this::get)
                .build();
    }

    private void get(String get) {
        sender().tell(count, self());
        saveSnapshot(count + 1);
    }


    @Override
    public String persistenceId() {
        return name;
    }
}
