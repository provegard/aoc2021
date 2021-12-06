object Day6 {
    // Increment the value of an entry in a Map<Int, Long>
    private fun inc(m: Map<Int, Long>, key: Int, delta: Long): Map<Int, Long> {
        val cur = m.getOrDefault(key, 0L)
        return m.plus(Pair(key, cur + delta))
    }

    class School(private val timers: Map<Int, Long> = mapOf()) {

        fun append(timer: Int): School {
            return append(timer, 1L)
        }

        private fun append(timer: Int, count: Long): School {
            return if (count == 0L) this else School(inc(timers, timer, count))
        }

        fun stepDay(): School {
            // Number of new fish is equal to the number of fish with value 0 before stepping
            val newCount = timers.getOrDefault(0, 0L)
            // Step fish, taking into account that 0->6 and 7->6 (so increment rather than just put)
            val newTimers = timers.entries.fold(mapOf<Int, Long>()) { nt, entry ->
                val newKey = if (entry.key == 0) 6 else (entry.key - 1)
                inc(nt, newKey, entry.value)
            }
            // New school with new fish appended
            return School(newTimers).append(8, newCount)
        }

        fun count() = timers.values.sum()
    }

    fun simulate(timers: List<Int>, days: Int): Long {
        val initial = timers.fold(School()) { acc, t -> acc.append(t) }
        val school = (0 until days).fold(initial) { acc, _ -> acc.stepDay() }
        return school.count()
    }
}

fun main() {
//    val numbers = listOf(3, 4, 3, 1, 2)

    val numbers = readLines("day6").first().split(",").map { it.toInt() }
    println(Day6.simulate(numbers, 80)) // 393019
    println(Day6.simulate(numbers, 256)) // 1757714216975
}