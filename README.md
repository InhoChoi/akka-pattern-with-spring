# Akka Pattern With Spring

## Hello Actor
- HelloActorRunner
- Autowire Spring Beans (HelloService)
- Akka Actor Ask Pattern

## Child Actor
- ChildActorRunner
- Parent - Child Actor

## Persistence Actor
- PersistenceActorRunner
- Local Level DB, Snapshot, Count


## Task Pattern
- Task Manager Actor, TestWorker Actor
- Task have state. Start, Stop
- Task start with point where it stop before.
- TODO
  - Redefine worker actor status
  - Add Pause Status
  - Add Finish Status
  - Graceful shutdown

## Rebalanceable Task Pattern with Akka Cluster
- TBD
- Akka Cluster, Akka Cluster Sharding, Persistence Actor

## Akka Stream with actor
- Akka Stream using actor
