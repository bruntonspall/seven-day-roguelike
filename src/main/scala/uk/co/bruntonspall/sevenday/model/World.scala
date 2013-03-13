package uk.co.bruntonspall.sevenday.model
import scala.collection.mutable

case class Glyph(glyph: Char) {
  def htmlGlyph: String = glyph match {
    case ' ' => "&nbsp;"
    case x => x.toString
  }
}

trait Renderable {
  def glyph: Glyph
  def render: String = glyph.htmlGlyph
}

case class BaseTile(passable: Boolean, openable: Boolean, glyph: Glyph) extends Renderable

object Tiles {
  val floor = BaseTile(passable = true, openable = false, Glyph('.'))
  val exitZone = BaseTile(passable = true, openable = false, Glyph('X'))
  val wall = BaseTile(passable = false, openable = false, Glyph('#'))
  val space = BaseTile(passable = false, openable = false, Glyph(' '))
  val door = BaseTile(passable = false, openable = true, Glyph('='))
  val all = floor :: wall :: space :: door :: exitZone :: Nil
  val charLookup = all.map { tile => tile.glyph.glyph -> tile }.toMap
}

case class MapElement(x: Int, y: Int, tile: BaseTile) extends Renderable {
  def glyph = tile.glyph
}

case class Mobile(id: Int, var x: Int, var y: Int, glyph: Glyph) extends Renderable

object World {
  val tiles: Seq[Seq[Char]] =
    List(
      "          #####         ",
      "          #...#         ",
      "          #...#         ",
      "          #...#         ",
      "          ##=###        ",
      "          #....#        ",
      "          #....#        ",
      "     #######=#######    ",
      "     #.............#    ",
      "     #.............#    ",
      "     #..########=###    ",
      "     #..#..#   #..#     ",
      "######..#..#   #..#     ",
      "XXX=.=..=..#####=####   ",
      "XXX=.=..#..#........#   ",
      "######..#..=........#   ",
      "   ####=####=###=##=### ",
      "   #.....#...#...#....# ",
      "   #.....#...#...#....# ",
      "   #.....#...#...#....# ",
      "   #.....#...#...#....# ",
      "   #################### ")

  def create() = {
    val map = for {
      y <- 0 to tiles.length - 1
    } yield {
      for (x <- 0 to tiles(y).length - 1)
        yield MapElement(x, y, Tiles.charLookup.getOrElse(tiles(y)(x), Tiles.space))
    }
    World(tiles(0).length, tiles.length, map)
  }

  lazy val map0 = create()
}

case class World(width: Int, height: Int,
    rows: Seq[Seq[MapElement]],
    charactersByLocation: mutable.Map[Tuple2[Int, Int], Mobile] = mutable.Map.empty,
    charactersById: mutable.Map[Int, Mobile] = mutable.Map.empty) {
  def getTileAt(x: Int, y: Int) = rows(y)(x)
  def getMobileAt(x: Int, y: Int) = charactersByLocation.get((x, y))
  def getMobile(id: Int) = charactersById.get(id)
  def createMobileAt(x: Int, y: Int, mobile: Mobile) = {
    charactersByLocation.put((x, y), mobile)
    charactersById.put(mobile.id, mobile)
  }
  def moveMobile(existing: Mobile, x: Int, y: Int) = {
    charactersByLocation.remove((existing.x, existing.y))
    existing.x = x
    existing.y = y
    charactersByLocation.put((x, y), existing)
  }
  def getRenderableAt(x: Int, y: Int) = getMobileAt(x, y).getOrElse(getTileAt(x, y))
}

