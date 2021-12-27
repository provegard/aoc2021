import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlin.math.max

typealias Die = Iterator<Int>

object Day21 {
    data class Player(val pos: Int, val score: Int) {
        fun turn(d: Die) = turn(d.take(3).toList())

        fun turn(moves: List<Int>): Player {
            val move = moves.sum()
            val newPos = (pos - 1 + move) % 10 + 1
            return Player(newPos, score + newPos)
        }

        fun winner() = score >= 1000
        fun winner2() = score >= 21
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

    private fun genOutcomes3(): Sequence<List<Int>> = sequence {
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    yield(listOf(i, j, k))
                }
            }
        }
    }
    private val outcomes3 = genOutcomes3().map { it.sorted() }.groupBy { it }

    private fun countNonWins(player: Player, outcomes: List<Int>, factor: Int, turns: Int, maxTurns: Int, nonWins: Long): Long {
        val (newPlayer, newTurns) = if (outcomes.size == 3) {
            Pair(player.turn(outcomes), turns + 1)
        } else Pair(player, turns)

        if (newPlayer.winner2()) {
            // not a non-win
            return nonWins
        }

        if (newTurns >= maxTurns) {
            return nonWins + 1L * factor
        }

        return outcomes3.entries.fold(nonWins) { acc, e ->
            countNonWins(newPlayer, e.key, factor * e.value.size, newTurns, maxTurns, acc)
        }
    }

    private fun calcWinsForTurns(player: Player, outcomes: List<Int>, factor: Int, turns: Int, wins: PersistentMap<Int, Long>): PersistentMap<Int, Long> {
        val (newPlayer, newTurns) = if (outcomes.size == 3) {
            Pair(player.turn(outcomes), turns + 1)
        } else Pair(player, turns)

        if (newPlayer.winner2()) {
            val oldWins = wins.getOrDefault(newTurns, 0L)
            return wins.put(newTurns, oldWins + 1L * factor)
        }

        return outcomes3.entries.fold(wins) { acc, e ->
            calcWinsForTurns(newPlayer, e.key, factor * e.value.size, newTurns, acc)
        }
    }

    private fun calcUniverses(wins: PersistentMap<Int, Long>, other: Player, maxDelta: Int): Long {
        return wins.entries.sumOf {
            val s = countNonWins(other, emptyList(), 1, 0, it.key + maxDelta, 0L)
            it.value * s
        }
    }

    fun part2(p1Pos: Int, p2Pos: Int): Long {

        val p1 = Player(p1Pos, 0)
        val p2 = Player(p2Pos, 0)
        val p1Wins = calcWinsForTurns(p1, emptyList(), 1, 0, persistentMapOf())
        val p2Wins = calcWinsForTurns(p2, emptyList(), 1, 0, persistentMapOf())

        // Since p1 goes first, it sufficient to count non-wins for equal number of turns minus one.
        // For p2, OTOH, p1 must non-win an equal number of turns.
        val p1u = calcUniverses(p1Wins, p2, -1)
        val p2u = calcUniverses(p2Wins, p1, 0)

        return max(p1u, p2u)
    }
}

fun main() {
    assert(739785, Day21.part1(4, 8), "Part 1 test")
    assert(671580, Day21.part1(7, 6), "Part 1")
    assert(444356092776315L, Day21.part2(4, 8), "Part 2 test")
    assert(912857726749764L, Day21.part2(7, 6), "Part 2")
}