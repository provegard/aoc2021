import java.util.*

object Day6 {

    class Fish(private var timer: Int) {
        fun countDown() {
            timer = if (timer == 0) 6 else timer - 1
        }

        fun isZero() = timer == 0
    }

    class School {
        private val list = LinkedList<Fish>()

        fun append(timer: Int) {
            list.addLast(Fish(timer))
        }

        fun stepDay() {
            val newCount = list.fold(0) { acc, fish ->
                val spawn = fish.isZero()
                fish.countDown()
                acc + if (spawn) 1 else 0
            }
            for (i in 0 until newCount) append(8)
        }

        fun count() = list.size
    }

    fun simulate(timers: List<Int>, days: Int): Int {
        val school = School()
        for (i in timers) school.append(i)
        for (i in 0 until days) school.stepDay()
        return school.count()
    }
}


fun main() {
    val numbers = listOf(3, 4, 3, 1, 2)

//    val numbers = readLines("day6").first().split(",").map { it.toInt() }
    println(Day6.simulate(numbers, 20))
}