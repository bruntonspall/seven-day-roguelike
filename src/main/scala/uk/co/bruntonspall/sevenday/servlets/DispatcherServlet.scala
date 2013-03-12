package uk.co.bruntonspall.sevenday.servlets

import org.scalatra.ScalatraServlet
import uk.co.bruntonspall.sevenday.scalatra.TwirlSupport
import uk.co.bruntonspall.sevenday.model._

object Renderer {
  def render(map: World, character: List[Mobile]): Seq[Seq[String]] = {
    for { y <- 0 to map.rows.length - 1 } yield {
      for { x <- 0 to map.rows(y).length - 1 } yield {
        character.find(c => c.x == x && c.y == y).map(_.htmlGlyph).getOrElse(map.getTileAt(x, y).htmlGlyph)
      }
    }
  }
}

class DispatcherServlet extends ScalatraServlet with TwirlSupport {

  get("/") {
    // We'l worry about sessions, users and stuff tomorrow
    // For now, we'll assume one single player (me), and one world
    // I suspect we'll want a compositing renderer at some point
    html.map.render(Renderer.render(World.map0, List(PlayerMobile(1, 13, '@', "@"))))
  }

  post("/execute") {
    contentType = "text/html"
    <ul>{
      params.keys.map(param => <li>{ param }</li>)
    }</ul>
  }

}
