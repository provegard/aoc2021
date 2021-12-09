object Day9 {

    data class Pos(val r: Int, val c: Int)

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

    fun part1(lines: List<String>): Int {
        val mat = lines.map { it.trim().chars().map { it - 48 }.toArray() }.toTypedArray()
        val low = allPositions(mat).filter { p -> adjacent(mat, p).all { ap -> mat[p.r][p.c] < mat[ap.r][ap.c] } }
        return low.sumOf { mat[it.r][it.c] + 1 }
    }
}

fun main() {
    val testLines = ("2199943210\n" +
            "3987894921\n" +
            "9856789892\n" +
            "8767896789\n" +
            "9899965678").split("\n")
    val lines = readLines("day9")

    println(Day9.part1(testLines))
    println(Day9.part1(lines))
}