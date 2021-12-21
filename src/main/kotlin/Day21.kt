typealias Die = Iterator<Int>

object Day21 {
    data class Player(val pos: Int, val score: Int) {
        fun turn(d: Die): Player {
            val move = d.take(3).sum()
            val newPos = (pos - 1 + move) % 10 + 1
            return Player(newPos, score + newPos)
        }

        fun winner() = score >= 1000
    }

    private fun practiceDie(): Die = generateSequence(1) { it + 1 }.iterator()

    fun part1(p1Pos: Int, p2Pos: Int): Int {
        val p1 = Player(p1Pos, 0)
        val p2 = Player(p2Pos, 0)
        val die = practiceDie()
        val result = generateSequence(p1 to p2) {
            val p1_ = it.first.turn(die)
            val p2_ = if (p1_.winner()) it.second else it.second.turn(die)
            p1_ to p2_
        }.first { it.first.winner() || it.second.winner() }
        val losingScore = if (result.first.winner()) result.second.score else result.first.score
        val next = die.take(1).single()
        return losingScore * (next - 1)
    }
}

fun main() {
    assert(739785, Day21.part1(4, 8), "Part 1 test")
    assert(671580, Day21.part1(7, 6), "Part 1")
}