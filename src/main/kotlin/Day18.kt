import Day18.Cell
import Day18.Number
import Day18.addAndReduce
import Day18.explode
import Day18.parse
import kotlin.math.roundToInt

object Day18 {
    interface HasDepth { val depth: Int }
    data class Cell(val num: Int, override val depth: Int): HasDepth {
        fun add(n: Int) = Cell(num + n, depth)
        fun oneDeeper() = Cell(num, depth + 1)
    }

    data class StringAtDepth(val s: String, override val depth: Int): HasDepth

    private fun <T> reduceToSingle(list: List<T>, createReplacement: (T, T) -> T): T where T : HasDepth {
        val maxDepth = list.maxOf { it.depth }
        val result = (maxDepth downTo 0).fold(list) { acc, depth ->
            generateSequence(acc) {
                val idx = it.indexOfFirst { x -> x.depth == depth }
                if (idx < 0) null else {
                    val (a, b) = it.drop(idx).take(2)
                    val replacement = createReplacement(a, b)
                    it.take(idx) + replacement + it.drop(idx + 2)
                }
            }.last()
        }
        return result.single()
    }

    data class Number(val cells: List<Cell>) {
        override fun toString(): String {
            val sads = cells.map { StringAtDepth("${it.num}", it.depth) }
            return reduceToSingle(sads) { a, b -> StringAtDepth("[${a.s},${b.s}]", a.depth - 1) }.s
        }

        fun magnitude(): Int =
            reduceToSingle(cells) { a, b -> Cell(3 * a.num + 2 * b.num, a.depth - 1) }.num
    }

    private fun add(a: Number, b: Number) = Number((a.cells + b.cells).map { it.oneDeeper() })

    fun explode(n: Number): Number? {
        val nested = n.cells.indexOfFirst { it.depth == 4 }
        if (nested < 0) return null
        val n1 = n.cells[nested].num
        val n2 = n.cells[nested + 1].num

        val leftIdx = nested - 1
        val rightIdx = nested + 2
        val before = if (leftIdx >= 0) (n.cells.take(leftIdx) + n.cells[leftIdx].add(n1)) else emptyList()
        val after = if (rightIdx < n.cells.size) (listOf(n.cells[rightIdx].add(n2)) + n.cells.drop(rightIdx + 1)) else emptyList()
        return Number(before + Cell(0, n.cells[nested].depth - 1) + after)
    }

    fun split(n: Number): Number? {
        val large = n.cells.indexOfFirst { it.num >= 10 }
        if (large < 0) return null
        val cell = n.cells[large]
        val p1 = Math.floorDiv(cell.num, 2)
        val p2 = (cell.num / 2.0).roundToInt()
        val newCells = listOf(Cell(p1, cell.depth + 1), Cell(p2, cell.depth + 1))
        return Number(n.cells.take(large) + newCells + n.cells.drop(large + 1))
    }

    private fun reduce(n: Number): Number = generateSequence(n) { explode(it) ?: split(it) }.last()

    fun addAndReduce(a: Number, b: Number) = reduce(add(a, b))

    fun parse(line: String): Number {
        val (cells, _) = line.toCharArray().fold(emptyList<Cell>() to -1) { acc, ch ->
            val (list, depth) = acc
            when (ch) {
                '[' -> list to (depth + 1)
                ']' -> list to (depth - 1)
                ',' -> list to depth
                else -> (list + Cell(ch.code - 48, depth)) to depth
            }
        }
        return Number(cells)
    }

    fun part1(lines: List<String>): Int {
        val numbers = lines.map { parse(it) }
        val result = numbers.drop(1).fold(numbers.first(), ::addAndReduce)
        return result.magnitude()
    }

    fun part2(lines: List<String>): Int {
        val numbers = lines.map { parse(it) }
        return numbers.pairs().maxOf { addAndReduce(it.first, it.second).magnitude() }
    }
}

fun main() {
    assert(
        Number(listOf(
        Cell(1, 0), Cell(2, 0)
    )), parse("[1,2]"), "test [1,2]")
    assert(
        Number(listOf(
        Cell(1, 1), Cell(2, 1), Cell(3, 0)
    )), parse("[[1,2],3]"), "test [[1,2],3]")
    assert(
        Number(listOf(
        Cell(9, 0), Cell(8, 1), Cell(7, 1)
    )), parse("[9,[8,7]]"), "test [9,[8,7]]")

    assert("[1,2]", parse("[1,2]").toString(), "test [1,2] toString")
    assert("[9,[8,7]]", parse("[9,[8,7]]").toString(), "test [9,[8,7]] toString")
    assert("[[1,2],3]", parse("[[1,2],3]").toString(), "test [[1,2],3] toString")
    assert("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]").toString(), "more toString")


    assert("[[[[0,9],2],3],4]", explode(parse("[[[[[9,8],1],2],3],4]")).toString(), "explode 1")
    assert("[7,[6,[5,[7,0]]]]", explode(parse("[7,[6,[5,[4,[3,2]]]]]")).toString(), "explode 2")
    assert("[[6,[5,[7,0]]],3]", explode(parse("[[6,[5,[4,[3,2]]]],1]")).toString(), "explode 3")
    assert("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", explode(parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")).toString(), "explode 4")
    assert("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", explode(parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")).toString(), "explode 5")

    assert("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", addAndReduce(parse("[[[[4,3],4],4],[7,[[8,4],9]]]"), parse("[1,1]")).toString(), "add and reduce test")

    val lines = readLines("day18")
    val testLines = readLines("day18_test")

    assert(4124, Day18.part1(lines), "Part 1")
    assert(3993, Day18.part2(testLines), "Part 2, test")
    assert(4673, Day18.part2(lines), "Part 2") // 4185 too low
}