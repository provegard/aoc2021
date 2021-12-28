import kotlin.system.measureTimeMillis

object Day25 {

    enum class CellType {
        FREE, EAST_FACING, SOUTH_FACING
    }

    data class SeaBottom(val map: Map<Coord, CellType>, val height: Int, val width: Int) {
        fun add(c: Coord, ct: CellType) = SeaBottom(map.plus(c to ct), height, width)

        private fun adjacent(c: Coord): Coord {
            val ct = map.getOrDefault(c, CellType.FREE)
            val (x, y) = when (ct) {
                CellType.EAST_FACING -> Coord(c.x + 1, c.y)
                CellType.SOUTH_FACING -> Coord(c.x, c.y + 1)
                else -> throw RuntimeException("Unknown: $ct")
            }
            return Coord(x % width, y % height)
        }

        fun move(ct: CellType): SeaBottom {
            val movable = map.entries.filter { it.value == ct }
                .map { it.key to adjacent(it.key) }
                .filter { !map.contains(it.second) }
            if (movable.isEmpty()) return this

            val cleared = movable.map { it.first }.toSet()
            val moved = movable.map { it.second to ct }
            val newMap = map.minus(cleared).plus(moved)
            return SeaBottom(newMap, height, width)
        }
    }

    private fun parse(lines: List<String>): SeaBottom {
        val initial = SeaBottom(mapOf(), lines.size, lines.first().length)
        return lines.foldIndexed(initial) { y, acc, line ->
            line.toCharArray().foldIndexed(acc) { x, acc2, ch ->
                val cellType = when (ch) {
                    '.' -> CellType.FREE
                    '>' -> CellType.EAST_FACING
                    'v' -> CellType.SOUTH_FACING
                    else -> throw RuntimeException("Unknown: $ch")
                }
                if (cellType != CellType.FREE) acc2.add(Coord(x, y), cellType) else acc2
            }
        }
    }

    private fun oneStep(sb: SeaBottom) = sb.move(CellType.EAST_FACING).move(CellType.SOUTH_FACING)

    private tailrec fun steps(sb: SeaBottom, count: Int): Int {
        val next = oneStep(sb)
        if (sb == next) return count + 1
        return steps(next, count + 1)
    }

    fun part1(lines: List<String>) = steps(parse(lines), 0)
}

fun main() {
    val testLines = readLines("day25_ex")
    val lines = readLines("day25")

    val ms = measureTimeMillis {
        assert(58, Day25.part1(testLines), "Part 1 test")
        assert(534, Day25.part1(lines), "Part 1")
    }
    println("Took $ms ms")
}