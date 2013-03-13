package uk.co.bruntonspall.sevenday.model
import scala.collection.mutable

case class Coord(x: Int, y: Int)

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
  val door = BaseTile(passable = true, openable = true, Glyph('='))
  val all = floor :: wall :: space :: door :: exitZone :: Nil
  val charLookup = all.map { tile => tile.glyph.glyph -> tile }.toMap
}

case class MapElement(x: Int, y: Int, tile: BaseTile) extends Renderable {
  def glyph = tile.glyph
}

case class Mobile(id: Int, var x: Int, var y: Int, glyph: Glyph) extends Renderable {
  def runAI = {
    // We don't do AI for the player
    if (id >= World.MONSTER_START_ID) {
      val dxy = for {
        dx <- List(-1, 0, 1)
        dy <- List(-1, 0, 1)
      } yield (dx, dy)
      val edges = dxy.filterNot { case (dx, dy) => dx == 0 && dy == 0 }
      val possibleTargets = edges.map { case (dx, dy) => World.map0.getTileAt(x + dx, y + dy) }.filter(_.tile.passable)
      val target = possibleTargets(World.rndNum(possibleTargets.length))
      println("Mobile " + this + " moves to " + target)
      World.map0.moveMobile(this, target.x, target.y)
    }
  }
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

  def create() = {
    val map = for {
      y <- 0 to tiles.length - 1
    } yield {
      for (x <- 0 to tiles(y).length - 1)
        yield MapElement(x, y, Tiles.charLookup.getOrElse(tiles(y)(x), Tiles.space))
    }
    World(tiles(0).length, tiles.length, map)
  }

  val MONSTER_START_ID = 1000

  lazy val map0 = create()

  def rndNum(max: Int): Int = math.floor(math.random * max).toInt

  def runAI() {
    map0.charactersById.values.foreach(_.runAI)
  }

}

case class World(width: Int, height: Int,
    rows: Seq[Seq[MapElement]],
    charactersByLocation: mutable.Map[Tuple2[Int, Int], Mobile] = mutable.Map.empty,
    charactersById: mutable.Map[Int, Mobile] = mutable.Map.empty) {

  def rndCoord(): Tuple2[Int, Int] = (World.rndNum(width), World.rndNum(height))

  def distributeMonsters() {
    val numberOfMobiles = World.rndNum(5) + 3
    for (i <- 0 to numberOfMobiles) {
      // Now find a place to put the monster.
      // Simple algorithm, iterate through random (x,y) coords until we find a passable one.
      val s = Stream.continually(rndCoord())
      val validTiles = s.filter { case (x, y) => getTileAt(x, y).tile.passable }
      val freeTiles = validTiles.filter { case (x, y) => getMobileAt(x, y).isEmpty }
      val location = freeTiles.head
      createMobileAt(World.MONSTER_START_ID + i, location._1, location._2, Glyph('M'))
    }
  }

  def getTileAt(x: Int, y: Int) = rows(y)(x)
  def getMobileAt(x: Int, y: Int) = charactersByLocation.get((x, y))
  def getMobile(id: Int) = charactersById.get(id)
  def createMobileAt(id: Int, x: Int, y: Int, glyph: Glyph) = {
    val mobile = Mobile(id, x, y, glyph)
    charactersByLocation.put((x, y), mobile)
    charactersById.put(mobile.id, mobile)
    mobile
  }
  def moveMobile(existing: Mobile, x: Int, y: Int) = {
    charactersByLocation.remove((existing.x, existing.y))
    existing.x = x
    existing.y = y
    charactersByLocation.put((x, y), existing)
  }
  def getRenderableAt(x: Int, y: Int) = getMobileAt(x, y).getOrElse(getTileAt(x, y))
}

