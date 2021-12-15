import kotlin.math.abs

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

    private fun manhattan(c1: Coord, c2: Coord): Int {
        return abs(c1.x - c2.x) + abs(c1.y - c2.y)
    }

    private fun aStar(start: Coord, goal: Coord, h: (Coord, Coord) -> Int, d: (Coord, Coord) -> Int, isValid: (Coord) -> Boolean): List<Coord> {
        val openSet = mutableSetOf(start)
        val cameFrom = mutableMapOf<Coord, Coord?>()
        val gScore = mutableMapOf(start to 0)
        val fScore = mutableMapOf(start to h(start, goal))

        while (openSet.isNotEmpty()) {
            val current = openSet.minByOrNull { fScore.getOrDefault(it, Int.MAX_VALUE) }!!
            if (current == goal) {
                var cur = current
                val path = mutableListOf(cur)
                while (cameFrom.contains(cur)) {
                    cur = cameFrom[cur]!!
                    path.add(0, cur)
                }
                return path
            }

            openSet.remove(current)
            for (neighbor in neighbors(current).filter { isValid(it) }) {
                val tentG = gScore.getOrDefault(current, Int.MAX_VALUE) + d(current, neighbor)
                if (tentG < gScore.getOrDefault(neighbor, Int.MAX_VALUE)) {
                    cameFrom[neighbor] = current
                    gScore[neighbor] = tentG
                    fScore[neighbor] = tentG + h(current, neighbor)
                    openSet.add(neighbor)
                }
            }
        }
        throw RuntimeException("Failed to reach end")
    }

    private fun aStar(map: Map<Coord, Int>): List<Coord> {
        val start = Coord(0, 0)
        val goal = Coord(map.keys.maxOf { it.x }, map.keys.maxOf { it.y })
        return aStar(start, goal, { a, b -> manhattan(a, b) }, { a, b -> map[b]!! }, { map.containsKey(it) })
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

    fun part1(lines: List<String>): Int {
        val map = parse(lines)
        val path = aStar(map)
        return path.drop(1).sumOf { map[it]!! }
    }

    fun part2(lines: List<String>): Int {
        val map = expand(parse(lines))
        val path = aStar(map)
        return path.drop(1).sumOf { map[it]!! }
    }
}

fun main() {
    val testLines = readLines("day15_ex")
    val lines = readLines("day15")

    assert(40, Day15.part1(testLines), "Part 1, example")
    assert(527, Day15.part1(lines), "Part 1")
    assert(315, Day15.part2(testLines), "Part 2, example")
    assert(2887, Day15.part2(lines), "Part 2")
}