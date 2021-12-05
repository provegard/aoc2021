import kotlin.math.sign

object Day5 {

    data class Point(val x: Int, val y: Int)

    data class Line(val p1: Point, val p2: Point) {
        val isStraight = p1.x == p2.x || p1.y == p2.y
    }

    private fun sgn(x: Int) = sign(x.toDouble()).toInt()

    private fun toLines(inputLines: List<String>): List<Line> {
        return inputLines.map {
            val parts = it.split("[ ,]".toRegex())
            val x1 = parts[0].toInt()
            val y1 = parts[1].toInt()
            val x2 = parts[3].toInt()
            val y2 = parts[4].toInt()
            Line(Point(x1, y1), Point(x2, y2))
        }
    }

    private fun points(line: Line): Sequence<Point> = sequence {
        val (x, y) = line.p1
        val dx = sgn(line.p2.x - x)
        val dy = sgn(line.p2.y - y)
        yield(line.p1)
        if (dx != 0 || dy != 0)
            yieldAll(points(Line(Point(x + dx, y + dy), line.p2)))
    }

    private fun getCounts(lines: List<Line>): Map<Point, Int> {
        return lines.flatMap { points(it) }.fold(mutableMapOf()) { acc, pt ->
            acc[pt] = acc.getOrDefault(pt, 0) + 1
            acc
        }
    }

    fun part1(lines: List<String>): Int {
        val counts = getCounts(toLines(lines).filter { it.isStraight })
        return counts.filter { it.value > 1 }.count()
    }

    fun part2(lines: List<String>): Int {
        val counts = getCounts(toLines(lines))
        return counts.filter { it.value > 1 }.count()
    }
}

fun main(args: Array<String>) {

    val lines = readLines("day5")

    println("Part 1 = ${Day5.part1(lines)}")
    println("Part 1 = ${Day5.part2(lines)}")
}