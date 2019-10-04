package example

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}

object TestShard {
  private val shardName = "MyShared"

  def startClusterSharding(nrOfShards: Int)(
    implicit system: ActorSystem
  ): ActorRef =
    ClusterSharding(system).start(
      typeName = shardName,
      entityProps = Props(new TestActor),
      settings = ClusterShardingSettings(system),
      extractEntityId = TestActor.extractEntityId,
      extractShardId = TestActor.extractShardId(nrOfShards),
    )

  def shardRegion(implicit system: ActorSystem): ActorRef =
    ClusterSharding(system).shardRegion(shardName)
}
