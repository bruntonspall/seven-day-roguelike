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

case class MobilePrototype(name: String, health: Int, resistance: Int, glyph: Glyph)

object MonsterTypes {
  val squadMember = MobilePrototype("Squaddie", 3, 1, Glyph('@'))
  val slobbering = MobilePrototype("Slobbering Alien", 2, 1, Glyph('A'))
  val fierce = MobilePrototype("Fierce Alien", 2, 2, Glyph('F'))
  val mother = MobilePrototype("Mother Alien", 3, 2, Glyph('M'))

}

case class Mobile(id: Int, var x: Int, var y: Int, var health: Int, template: MobilePrototype) extends Renderable {
  def name = template.name
  def glyph = template.glyph
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
  def damage(dam: Int): String = {
    if (dam == 0)
      "A glancing shot"
    else if (dam <= template.resistance)
      "minor scratches"
    else {
      health -= dam - template.resistance
      health match {
        case 0 => "spraying blood everywhere"
        case 1 => "oozing ichor"
        case _ => "a clean shot"
      }
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
      x <- 0 to tiles(y).length - 1
    } yield (x, y) -> MapElement(x, y, Tiles.charLookup.getOrElse(tiles(y)(x), Tiles.space))
    World(tiles(0).length, tiles.length, map.toMap)
  }

  val MONSTER_START_ID = 1000

  lazy val map0 = create()

  def rndNum(max: Int): Int = math.floor(math.random * max).toInt

  def runTurn() {
    // Remove dead creatures and update the status messages
    map0.charactersById.values.foreach { c =>
      if (c.health <= 0) {
        World.map0.removeMobile(c)
        World.map0.addStatusLine("You killed a " + c.name)
      }
    }
    map0.charactersById.values.foreach(_.runAI)
    map0.clearVisibility()
  }

  def line(x1: Int, y1: Int, x2: Int, y2: Int): List[(Int, Int)] = {
    // Simple line of sight algorithm
    // Draw a straight line from (x1,y1) to (x2,y2)
    // If all tiles are passable, return true
    val dx = math.abs(x2 - x1)
    val dy = math.abs(y2 - y1)
    val sx = if (x1 < x2) 1 else -1
    val sy = if (y1 < y2) 1 else -1
    @annotation.tailrec
    def recLine(x: Int, y: Int, e: Double, acc: List[(Int, Int)]): List[(Int, Int)] = {
      if (x == x2 && y == y2) (x, y) :: acc
      else
        recLine(
          if (e > -dx) x + sx else x,
          if (e < dy) y + sy else y,
          e + (if (e > -dx) -dy else 0) + (if (e < dy) dx else 0),
          (x, y) :: acc)
    }
    val err = (if (dx > dy) dx else -dy) / 2
    recLine(x1, y1, err, List()).reverse
  }

}

case class World(width: Int, height: Int,
    private val rows: Map[Tuple2[Int, Int], MapElement],
    charactersByLocation: mutable.Map[Tuple2[Int, Int], Mobile] = mutable.Map.empty,
    charactersById: mutable.Map[Int, Mobile] = mutable.Map.empty,
    var statusMessages: List[String] = List.empty) {
  val visibilityMap: mutable.Map[(Int, Int), Boolean] = mutable.Map.empty
  def addStatusLine(s: String) {
    statusMessages = s :: statusMessages
  }

  def getDisplayClass(x: Int, y: Int) = {
    "glyph" ::
      (if (visibleToPlayer(x, y)) "visible" else "") ::
      (if (containsPlayer(x, y)) "player" + getMobileAt(x, y).get.id else "") ::
      Nil
  }

  def rndCoord(): Tuple2[Int, Int] = (World.rndNum(width), World.rndNum(height))

  def distributeMonsters() {
    // Let's distribute monster types too
    // We want 1 mother, 1-2 Fierce's, and between 5 and 8 Slobberings
    val mobTemplates =
      List(MonsterTypes.mother) :::
        Stream.continually(MonsterTypes.fierce).take(World.rndNum(2) + 1).toList :::
        Stream.continually(MonsterTypes.slobbering).take(World.rndNum(5) + 3).toList
    for ((prototype, i) <- mobTemplates.zipWithIndex) {
      // Now find a place to put the monster.
      // Simple algorithm, iterate through random (x,y) coords until we find a passable one.
      val s = Stream.continually(rndCoord())
      val validTiles = s.filter { case (x, y) => getTileAt(x, y).tile.passable }
      val freeTiles = validTiles.filter { case (x, y) => getMobileAt(x, y).isEmpty }
      val location = freeTiles.head

      createMobileAt(World.MONSTER_START_ID + i, location._1, location._2, prototype)
    }
  }

  def getTileAt(x: Int, y: Int): MapElement = rows.getOrElse((x, y), MapElement(x, y, Tiles.space))
  //rows(x)(y) 
  //rows.getOrElse(y, List()).getOrElse(x, Tiles.space)
  def getMobileAt(x: Int, y: Int) = charactersByLocation.get((x, y))
  def getMobile(id: Int) = charactersById.get(id)
  def containsPlayer(x: Int, y: Int) = getMobileAt(x, y).map { _.template == MonsterTypes.squadMember }.getOrElse(false)

  def clearVisibility() {
    visibilityMap.clear
  }

  def visibleToPlayer(x: Int, y: Int): Boolean = {
    val visOpt = visibilityMap.get((x, y))
    if (visOpt.isDefined) visOpt.get
    else {
      val mob = getMobile(1).get
      val visibility = World
        .line(mob.x, mob.y, x, y)
        .map { case (tx, ty) => (tx, ty) -> getTileAt(tx, ty).tile.passable }
      visibility
        .takeWhile { case (_, vis) => vis }
        .foreach { case ((x, y), vis) => visibilityMap.put((x, y), vis) }

      visibilityMap.get((x, y)).getOrElse(false)
    }
  }

  def lineOfSight(x1: Int, y1: Int, x2: Int, y2: Int): Boolean = {
    val passing = World.line(x1, y1, x2, y2).map { case (tx, ty) => getTileAt(tx, ty).tile.passable }
    !passing.contains(false)
  }

  def createMobileAt(id: Int, x: Int, y: Int, prototype: MobilePrototype) = {
    println("Creating mobile: " + prototype + " at (" + x + "," + y + ")")
    val mobile = Mobile(id, x, y, prototype.health, prototype)
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
  def removeMobile(mobile: Mobile) {
    charactersById.remove(mobile.id)
    charactersByLocation.remove((mobile.x, mobile.y))
  }

  def getRenderableAt(x: Int, y: Int) = getMobileAt(x, y).getOrElse(getTileAt(x, y))
}

