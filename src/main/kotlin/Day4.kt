object Day4 {

    class Board(private val state: List<List<Int>>) {

        private constructor(state: List<List<Int>>, mn: Set<Int>) : this(state) {
            this.markedNumbers = mn
        }

        private var markedNumbers: Set<Int> = emptySet()

        fun markNumber(n: Int): Board {
            val newSet = markedNumbers.plus(n)
            return Board(state, newSet)
        }

        fun isWin(): Boolean {
            return rowsAndColumns().any { it.all { n -> markedNumbers.contains(n) } }
        }

        fun unmarkedSum(): Int {
            return rowsAndColumns().flatten().filter { !markedNumbers.contains(it) }.distinct().sum()
        }

        private fun rowsAndColumns(): List<List<Int>> = rows() + columns()
        private fun rows(): List<List<Int>> = state
        private fun columns(): List<List<Int>> =
            state.indices.map { i -> state.map { it[i] }}
    }

    class World(private val boards: List<Board>, private val numbers: List<Int>, private val prevNumber: Int) {

        fun hasWinner(): Boolean {
            return boards.any { it.isWin() }
        }

        fun isDone(): Boolean {
            return numbers.isEmpty()
        }

        fun calculate(): Int {
            val winner = boards.first { it.isWin() }
            return winner.unmarkedSum() * prevNumber
        }

        fun step(): World {
            val num = numbers.first()
            val newBoards = boards.map { it.markNumber(num) }
            return World(newBoards, numbers.drop(1), num)
        }

        fun dropWinners(): World {
            val rest = boards.filterNot { it.isWin() }
            return World(rest, numbers, 0)
        }
    }

    private fun readBoardLines(lines: List<String>): List<List<Int>> {
        if (lines.isEmpty()) return emptyList()
        val line = lines.first().trim()
        if (line == "") return emptyList()
        val numbers = line.split("[ ]+".toRegex()).map { it.toInt() }
        return listOf(numbers).plus(readBoardLines(lines.drop(1)))
    }

    private fun readBoards(lines: List<String>): List<Board> {
        if (lines.isEmpty()) return emptyList()
        val boardLines = readBoardLines(lines)
        val next = lines.drop(1 + boardLines.size)
        return listOf(Board(boardLines)).plus(readBoards(next))
    }

    private fun createWorld(lines: List<String>): World {
        val numbers = lines.first().split(",").map { it.toInt() }
        val boards = readBoards(lines.drop(1))
        return World(boards, numbers, -1)
    }

    private fun wins(world: World): Sequence<Int> = sequence {
        if (!world.isDone()) {
            if (world.hasWinner())
                yield(world.calculate())
            val next = world.dropWinners().step()
            yieldAll(wins(next))
        }
    }

    fun part1(lines: List<String>): Int {
        val world = createWorld(lines)
        return wins(world).first()
    }

    fun part2(lines: List<String>): Int {
        val world = createWorld(lines)
        return wins(world).last()
    }
}

fun main(args: Array<String>) {

    val lines = readLines("day4")

    println("Part 1 = ${Day4.part1(lines)} (11536)")
    println("Part 2 = ${Day4.part2(lines)} (1284)")
}