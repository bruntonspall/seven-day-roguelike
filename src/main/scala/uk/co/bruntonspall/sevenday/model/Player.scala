package uk.co.bruntonspall.sevenday.model

abstract class Mobile {
  def x: Int
  def y: Int
  def glyph: Char
  def htmlGlyph: String
}

case class PlayerMobile(x: Int, y: Int, glyph: Char, htmlGlyph: String) extends Mobile
object Player {
  lazy val character = PlayerMobile(1, 13, '@', "@")
}
