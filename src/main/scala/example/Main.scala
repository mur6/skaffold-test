package example

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.stream.ActorMaterializer
import scala.concurrent.duration._

object Main extends App {
  implicit val system = ActorSystem("akka-cluster-test")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val clusterSharded = TestShard.startClusterSharding(20)
  implicit val requestTimeout = akka.util.Timeout(3 second)
  val random = scala.util.Random
  val hostname = sys.env("HOSTNAME")
  val route =
    pathSingleSlash {
      get {
        val id = random.nextInt(20).toString
        val response = clusterSharded.ask(TestActor.Command(id)).mapTo[TestActor.Response]
        onSuccess(response) { r =>
          val msg = s"[$hostname] Generated id=$id responsed-node=${r.hostname}"
          complete(msg)
        }
      }
    }
  AkkaManagement(system).start()
  ClusterBootstrap(system).start()
  Http().bindAndHandle(route, "0.0.0.0", 8080)
  println(s"Server start...")
}
