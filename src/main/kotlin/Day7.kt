import kotlin.math.abs

object Day7 {

    private fun minMax(positions: List<Int>): Pair<Int, Int> {
        val min = positions.minOrNull() ?: 0
        val max = positions.maxOrNull() ?: 0
        return Pair(min, max)
    }

    fun part1(positions: List<Int>): Int {
        val (min, max) = minMax(positions)
        return (min..max).map { positions.sumOf { p -> abs(p - it) } }.minOrNull() ?: 0
    }

    private fun cost(a: Int, b: Int): Int {
        val n = abs(b - a)
        return n * (n + 1) / 2
    }

    fun part2(positions: List<Int>): Int {
        val (min, max) = minMax(positions)
        return (min..max).map { positions.sumOf { p -> cost(p, it) } }.minOrNull() ?: 0
    }
}

fun main() {
//    val numbers = "16,1,2,0,4,2,7,1,2,14".split(",").map { it.toInt() }
    val numbers = readLines("day7").first().split(",").map { it.toInt() }
    println("Part 1 = " + Day7.part1(numbers))
    println("Part 2 = " + Day7.part2(numbers))
}