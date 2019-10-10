package example

import akka.actor.{Actor, ActorLogging}
import akka.cluster.sharding.ShardRegion


object TestActor {
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.id.toString, cmd)
  }

  def extractShardId(nrOfShards: Int): ShardRegion.ExtractShardId = {
    case cmd: Command =>
      (Math.abs(cmd.id.hashCode()) % nrOfShards).toString
  }

  case class Command(id: String)
  case class Response(id: String, hostname: String)
}

class TestActor extends Actor with ActorLogging {
  import TestActor._

  val hostname = sys.env("HOSTNAME")

  override def receive: Receive = {
    case Command(id) =>
      val resp = Response(id, hostname)
      println(s"Received: ${resp}")
      sender() ! resp
  }
}
