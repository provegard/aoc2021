import kotlinx.collections.immutable.*

object Day11 {
    private fun isValidPos(pos: Pos): Boolean {
        return pos.r in 0..9 && pos.c in 0..9
    }

    private fun adjacent(pos: Pos): List<Pos> {
        return (-1..1).flatMap { r -> (-1..1).map { c -> Pair(r, c) } }
            .filter { it != Pair(0, 0) }
            .map { Pos(pos.r + it.first, pos.c + it.second) }
            .filter { isValidPos(it) }
    }

    private fun toMat(lines: List<String>): Array<IntArray> {
        return lines.map { it.trim().chars().map { it - 48 }.toArray() }.toTypedArray()
    }

    private fun flash(map: PersistentMap<Pos, Int>, visited: PersistentSet<Pos>): PersistentMap<Pos, Int> {
        val flashingPositions = map.entries.filter { it.value > 9 && !visited.contains(it.key) }.map { it.key }
        if (flashingPositions.isEmpty()) return map

        val allAdjacent = flashingPositions.flatMap { adjacent(it) }
        val incremented = allAdjacent.fold(map) { acc, pos ->
            acc.put(pos, acc[pos]!! + 1)
        }
        return flash(incremented, visited + flashingPositions)
    }

    private fun reset(map: PersistentMap<Pos, Int>): Pair<PersistentMap<Pos, Int>, Int> {
        val flashed = map.entries.filter { it.value > 9 }
        val newMap = flashed.fold(map) { acc, e ->
            acc.put(e.key, 0)
        }
        return Pair(newMap, flashed.size)
    }

    private fun emptyPersistentSetOfPos()
        = emptySet<Pos>().toPersistentSet()

    private fun step(map: PersistentMap<Pos, Int>): Pair<PersistentMap<Pos, Int>, Int> {
        val increased = map.entries.fold(map) { acc, e ->
            acc.put(e.key, e.value + 1)
        }
        return reset(
            flash(
                increased,
                emptyPersistentSetOfPos()
            )
        )
    }

    private fun toMap(lines: List<String>): PersistentMap<Pos, Int> {
        val mat = toMat(lines)
        val map = (mat.indices).flatMap { r -> (0 until mat[r].size).map { c -> Pos(r, c) to mat[r][c] } }.toMap().toPersistentMap()
        return map
    }

    private fun steps(map: PersistentMap<Pos, Int>, fl: Int = 0): Sequence<Pair<PersistentMap<Pos, Int>, Int>> = sequence {
        val (newMap, flashed) = step(map)
        yield(Pair(newMap, fl + flashed))
        yieldAll(steps(newMap, fl + flashed))
    }

    private fun allFlashed(map: PersistentMap<Pos, Int>): Boolean {
        return map.entries.all { it.value == 0 }
    }

    fun part1(lines: List<String>, stepCount: Int = 100): Int {
        val map = toMap(lines)
        return steps(map).take(stepCount).last().second
    }

    fun part2(lines: List<String>): Int {
        val map = toMap(lines)
        return steps(map).withIndex().dropWhile { !allFlashed(it.value.first) }.first().index + 1
    }
}

fun main() {
    val testLines = ("5483143223\n" +
            "2745854711\n" +
            "5264556173\n" +
            "6141336146\n" +
            "6357385478\n" +
            "4167524645\n" +
            "2176841721\n" +
            "6882881134\n" +
            "4846848554\n" +
            "5283751526").split("\n")
    val lines = readLines("day11")

    assert(35, Day11.part1(testLines, 2), "Part 1, test reduced")
    assert(1656, Day11.part1(testLines), "Part 1, test")
    assert(1700, Day11.part1(lines), "Part 1")
    assert(195, Day11.part2(testLines), "Part 2, test")
    assert(273, Day11.part2(lines), "Part 2")
}