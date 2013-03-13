package uk.co.bruntonspall.sevenday.servlets

import org.scalatra.ScalatraServlet
import uk.co.bruntonspall.sevenday.scalatra.TwirlSupport
import uk.co.bruntonspall.sevenday.model._
import cc.spray.json._
import scala.math.abs
import javax.servlet.ServletConfig

case class Action(character: Int, x: Int, y: Int)
object ActionProtocol extends DefaultJsonProtocol {
  implicit val actionFormat = jsonFormat3(Action)
}

import ActionProtocol._

class DispatcherServlet extends ScalatraServlet with TwirlSupport {
  val world = World.map0

  override def initialize(config: ServletConfig): Unit = {
    super.initialize(config)
    world.createMobileAt(1, 1, 13, Glyph('@'))
    world.distributeMonsters()
  }

  get("/") {
    // We'l worry about sessions, users and stuff tomorrow
    // For now, we'll assume one single player (me), and one world
    // I suspect we'll want a compositing renderer at some point
    html.map.render(world)
  }

  post("/execute") {
    val actions = params("actions").asJson.convertTo[List[Action]]
    contentType = "text/json"
    actions.foreach { action =>
      {
        println("Processing action " + action)
        val player = world.getMobile(action.character).get
        println("Player is currently: " + player)
        println("Target tile is :" + world.getTileAt(action.x, action.y))
        if (abs(player.x - action.x) <= 1 &&
          abs(player.y - action.y) <= 1 &&
          world.getTileAt(action.x, action.y).tile.passable == true) {
          println("Moving player")
          world.moveMobile(player, action.x, action.y)
        }
      }

    }
  }

}
