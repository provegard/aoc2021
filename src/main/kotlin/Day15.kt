import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day15 {

    private fun parse(lines: List<String>): Map<Coord, Int> {
        return lines.indices.fold(emptyMap()) { map, y ->
            val line = lines[y]
            line.indices.fold(map) { mp, x ->
                mp.plus(Coord(x, y) to (line[x].code - 48))
            }
        }
    }

    private fun neighbors(c: Coord): List<Coord> {
        return listOf(
            Coord(c.x - 1, c.y),
            Coord(c.x + 1, c.y),
            Coord(c.x, c.y - 1),
            Coord(c.x, c.y + 1)
        )
    }

    private fun manhattan(c1: Coord, c2: Coord) = abs(c1.x - c2.x) + abs(c1.y - c2.y)

    private fun aStar(map: Map<Coord, Int>): List<Coord> {
        val start = Coord(0, 0)
        val goal = Coord(map.keys.maxOf { it.x }, map.keys.maxOf { it.y })
        return aStar(start, goal, ::manhattan, { _, b -> map[b]!! }, { neighbors(it).filter { n -> map.containsKey(n) } })
    }

    private fun incRisk(risk: Int, add: Int): Int {
        val newRisk = risk + add
        return if (newRisk <= 9) newRisk else newRisk - 9
    }

    private fun expand(map: Map<Coord, Int>): Map<Coord, Int> {
        val width = map.keys.maxOf { it.x } + 1
        val height = map.keys.maxOf { it.y } + 1
        val newEntries = map.keys.flatMap { c ->
            (0..4).flatMap { dy ->
                (0..4).map { dx ->
                    val newCoord = Coord(c.x + dx * width, c.y + dy * height)
                    val risk = map[c]!!
                    val newRisk = incRisk(incRisk(risk, dy), dx)
                    newCoord to newRisk
                }
            }
        }
        return newEntries.toMap()
    }

    private fun findPathRisk(map: Map<Coord, Int>): Int {
        val path = aStar(map)
        return path.drop(1).sumOf { map[it]!! }
    }

    fun part1(lines: List<String>): Int = findPathRisk(parse(lines))

    fun part2(lines: List<String>): Int = findPathRisk(expand(parse(lines)))
}

fun main() {
    val testLines = readLines("day15_ex")
    val lines = readLines("day15")

    assert(40, Day15.part1(testLines), "Part 1, example")
    assert(527, Day15.part1(lines), "Part 1")
    assert(315, Day15.part2(testLines), "Part 2, example")

    val ms = measureTimeMillis {
        assert(2887, Day15.part2(lines), "Part 2")
    }
    println("Part 2 took $ms ms")
}