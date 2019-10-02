package example

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.stream.ActorMaterializer

object Main extends App {
  implicit val system = ActorSystem("akka-cluster-test")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val cluster = Cluster(system)

  val route =
    pathSingleSlash {
      get {
        complete {
          "hello!"
        }
      }
    }
  AkkaManagement(system).start()
  ClusterBootstrap(system).start()
  Http().bindAndHandle(route, "0.0.0.0", 8080)
  println(s"Server start...")
}
