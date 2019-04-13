package com.inhochoi.springakka.persistence;

import akka.persistence.SelectedSnapshot;
import akka.persistence.SnapshotMetadata;
import akka.persistence.SnapshotSelectionCriteria;
import akka.persistence.snapshot.japi.SnapshotStore;
import scala.concurrent.Future;

import java.util.Optional;

public class FileSnapshotStore extends SnapshotStore {
    @Override
    public Future<Optional<SelectedSnapshot>> doLoadAsync(String persistenceId, SnapshotSelectionCriteria criteria) {
        return null;
    }

    @Override
    public Future<Void> doSaveAsync(SnapshotMetadata metadata, Object snapshot) {
        return null;
    }

    @Override
    public Future<Void> doDeleteAsync(SnapshotMetadata metadata) {
        return null;
    }

    @Override
    public Future<Void> doDeleteAsync(String persistenceId, SnapshotSelectionCriteria criteria) {
        return null;
    }
}
