package uk.co.bruntonspall.sevenday.servlets

import org.scalatra.ScalatraServlet
import uk.co.bruntonspall.sevenday.scalatra.TwirlSupport
import uk.co.bruntonspall.sevenday.model._
import cc.spray.json._
import scala.math.abs

object Renderer {
  def render(map: World, character: List[Mobile]): Seq[Seq[String]] = {
    for { y <- 0 to map.rows.length - 1 } yield {
      for { x <- 0 to map.rows(y).length - 1 } yield {
        character.find(c => c.x == x && c.y == y).map(_.htmlGlyph).getOrElse(map.getTileAt(x, y).htmlGlyph)
      }
    }
  }
}

case class Action(character: Int, x: Int, y: Int)
object ActionProtocol extends DefaultJsonProtocol {
  implicit val actionFormat = jsonFormat3(Action)
}

import ActionProtocol._

class DispatcherServlet extends ScalatraServlet with TwirlSupport {
  val world = World.map0
  var player = PlayerMobile(1, 13, '@', "@")
  val characters = List(player)

  get("/") {
    // We'l worry about sessions, users and stuff tomorrow
    // For now, we'll assume one single player (me), and one world
    // I suspect we'll want a compositing renderer at some point
    html.map.render(Renderer.render(world, List(player)))
  }

  post("/execute") {
    params.keys.map { println }
    val actions = params("actions").asJson.convertTo[List[Action]]
    contentType = "text/json"
    actions.foreach { action =>
      if (abs(player.x - action.x) <= 1 &&
        abs(player.y - action.y) <= 1 &&
        world.getTileAt(action.x, action.y).passable == true)
        player = PlayerMobile(action.x, action.y, player.glyph, player.htmlGlyph)

    }
  }

}
