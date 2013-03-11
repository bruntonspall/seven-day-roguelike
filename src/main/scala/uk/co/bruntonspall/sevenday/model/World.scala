package uk.co.bruntonspall.sevenday.model

abstract class BaseTile {
  def passable: Boolean
  def openable: Boolean
  def glyph: Char
}

object FloorTile extends BaseTile {
  val glyph: Char = '.'
  val passable = true
  val openable = false
}
object WallTile extends BaseTile {
  val glyph = '#'
  val passable = false
  val openable = false
}
object SpaceTile extends BaseTile {
  val glyph = ' '
  val passable = false
  val openable = false
}
object DoorTile extends BaseTile {
  val glyph = '='
  val passable = false
  val openable = true
}

case class World(val width: Int, val height: Int, tiles: Seq[BaseTile]) {
  def getTileAt(x: Int, y: Int) = tiles(width * y + x)
}

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

  def tileObjs = List(FloorTile, SpaceTile, WallTile, DoorTile)
  def lookup(glyph: Char): List[BaseTile] =
    for { tile <- tileObjs if tile.glyph == glyph } yield tile
  def create() = {
    val map = for {
      row <- tiles
      tile <- row
    } yield lookup(tile).headOption.getOrElse(SpaceTile)
    World(tiles(0).length, tiles.length, map)
  }

  lazy val map0 = create()
}

