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

  override def initialize(config: ServletConfig) {
    super.initialize(config)
    world.createMobileAt(1, 1, 13, MonsterTypes.squadMember)
    world.createMobileAt(2, 1, 14, MonsterTypes.squadMember)
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
        if (world.visibleToPlayer(action.x, action.y)) {
          val targetOpt = world.getMobileAt(action.x, action.y)
          targetOpt match {
            case Some(target) => {
              // Shoot at the target.
              println("Shooting at " + target)
              val hit = World.rndNum(5)
              val damage = World.rndNum(6)
              println("HIt: " + hit + " - Damage: " + damage)
              if (hit > 0) {
                val description = target.damage(damage)
                println("Damage done to " + target)
                world.addStatusLine("You hit the " + target.name + " - " + description)
              }
            }
            case _ => {
              if (abs(player.x - action.x) <= 1 &&
                abs(player.y - action.y) <= 1 &&
                world.getTileAt(action.x, action.y).tile.passable == true) {
                println("Moving player")
                world.moveMobile(player, action.x, action.y)
              }
            }
          }
        } else { println("Can't see target") }
      }
    }
    World.runTurn
  }

}
