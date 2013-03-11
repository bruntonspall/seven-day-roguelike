package uk.co.bruntonspall.sevenday.model

case class Player(var x: Int, var y: Int)
object Player {
  lazy val character = Player(0, 7)
}
