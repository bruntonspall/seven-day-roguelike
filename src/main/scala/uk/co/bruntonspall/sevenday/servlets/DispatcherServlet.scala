package uk.co.bruntonspall.sevenday.servlets

import org.scalatra.ScalatraServlet
import uk.co.bruntonspall.sevenday.scalatra.TwirlSupport
import uk.co.bruntonspall.sevenday.model._

object Renderer {
  def render(map: World, character: Player): Seq[Seq[String]] = {
    for (row <- map.rows) yield {
      for (cell <- row) yield cell.htmlGlyph
    }
  }
}

class DispatcherServlet extends ScalatraServlet with TwirlSupport {

  get("/") {
    // We'l worry about sessions, users and stuff tomorrow
    // For now, we'll assume one single player (me), and one world
    // I suspect we'll want a compositing renderer at some point
    html.map.render(Renderer.render(World.map0, Player.character))
  }

}
