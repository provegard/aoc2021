object Day9 {

    private fun isValidPos(mat: Array<IntArray>, pos: Pos): Boolean {
        return pos.r >= 0 && pos.r < mat.size && pos.c >= 0 && pos.c < mat[0].size
    }

    private fun allPositions(mat: Array<IntArray>): List<Pos> {
        return mat.indices.flatMap { r -> mat[r].indices.map { c -> Pos(r, c) } }
    }

    private fun adjacent(mat: Array<IntArray>, pos: Pos): List<Pos> {
        val positions = listOf(
            Pos(pos.r - 1, pos.c),
            Pos(pos.r + 1, pos.c),
            Pos(pos.r, pos.c - 1),
            Pos(pos.r, pos.c + 1)
        )
        return positions.filter { isValidPos(mat, it) } //.map { mat[it.r][it.c] }
    }

    private fun toMat(lines: List<String>): Array<IntArray> {
        return lines.map { it.trim().chars().map { it - 48 }.toArray() }.toTypedArray()
    }

    private fun lowPoints(mat: Array<IntArray>): List<Pos> {
        return allPositions(mat).filter { p -> adjacent(mat, p).all { ap -> mat[p.r][p.c] < mat[ap.r][ap.c] } }
    }

    private fun findBasinRec(mat: Array<IntArray>, pos: Pos, visited: Set<Pos>): Sequence<Pos> = sequence {
        if (mat[pos.r][pos.c] < 9) {
            yield(pos)
            val adj = adjacent(mat, pos)
                .filter { !visited.contains(it) }
                .filter { ap -> mat[ap.r][ap.c] > mat[pos.r][pos.c] }
            val newVisited = visited.plus(pos).plus(adj)
            yieldAll(adj.flatMap { findBasinRec(mat, it, newVisited) })
        }
    }

    private fun findBasin(mat: Array<IntArray>, start: Pos): List<Pos> {
        return findBasinRec(mat, start, emptySet()).distinct().toList()
    }

    fun part1(lines: List<String>): Int {
        val mat = toMat(lines)
        return lowPoints(mat).sumOf { mat[it.r][it.c] + 1 }
    }

    fun part2(lines: List<String>): Int {
        val mat = toMat(lines)
        val low = lowPoints(mat)
        val largestBasins = low.map { findBasin(mat, it) }.sortedByDescending { it.size }.take(3)
        val sizes = largestBasins.map { it.size }
        return sizes.fold(1) { acc, s -> acc * s }
    }
}

fun main() {
    val testLines = ("2199943210\n" +
            "3987894921\n" +
            "9856789892\n" +
            "8767896789\n" +
            "9899965678").split("\n")
    val lines = readLines("day9")

    assert(15, Day9.part1(testLines), "part 1, test")
    assert(522, Day9.part1(lines), "part 1")
    assert(1134, Day9.part2(testLines), "part 2, test")
    assert(916688, Day9.part2(lines), "part 2")
}