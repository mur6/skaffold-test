package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object Main extends App {
  implicit val system = ActorSystem("akka-cluster-test")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route =
    pathSingleSlash {
      get {
        complete {
          "hello!"
        }
      }
    }
  Http().bindAndHandle(route, "0.0.0.0", 8080)
  println(s"Server start...")
}
