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

    private fun findPath(map: Map<Coord, Int>, start: Coord, end: Coord): List<Coord> {
        val q = mutableSetOf<Coord>()
        val dist = mutableMapOf<Coord, Int>()
        val prev = mutableMapOf<Coord, Coord?>()
        for (v in map.keys) {
            dist[v] = Int.MAX_VALUE
            prev[v] = null
            q.add(v)
        }
        dist[start] = 0

        while (q.isNotEmpty()) {
            val u = q.minByOrNull { dist[it]!! }!!
            q.remove(u)

            if (u == end) {
                // Found shortest
                val s = mutableListOf<Coord>()
                var u_: Coord? = u
                while (u_ != null) {
                    s.add(0, u_)
                    u_ = prev[u_]
                }
                return s
            }

            for (v in neighbors(u).filter { q.contains(it) }) {
                val alt = dist[u]!! + map[v]!! // edge length = risk to enter
                if (alt < dist[v]!!) {
                    dist[v] = alt
                    prev[v] = u
                }
            }
        }
        throw RuntimeException("Didn't reach end")
    }

    fun part1(lines: List<String>): Int {
        val map = parse(lines)
        val start = Coord(0, 0)
        val end = Coord(lines.first().length - 1, lines.size - 1)
        val path = findPath(map, start, end)
        return path.drop(1).sumOf { map[it]!! }
    }
}

fun main() {
    val testLines = readLines("day15_ex")
    val lines = readLines("day15")

    assert(40, Day15.part1(testLines), "Part 1, example")
    assert(527, Day15.part1(lines), "Part 1")
}