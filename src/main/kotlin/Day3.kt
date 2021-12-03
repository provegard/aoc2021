object Day3 {

    private fun getBit(i: Int, size: Int, numbers: List<Int>, pick: (Int) -> Boolean): Int {
        val mask = 1.shl(size - i)
        val ones = numbers.count { it.and(mask) != 0 }
        val zeroes = numbers.size - ones
        return if (pick(ones.compareTo(zeroes))) 1 else 0
    }

    private fun simpleRate(numbers: List<Int>, size: Int, pick: (Int) -> Boolean): Int {
        return (1..size).fold(0) { d, i ->
            d.shl(1) + getBit(i, size, numbers, pick)
        }
    }

    fun part1(numbers: List<Int>, size: Int): Int {
        val gamma = simpleRate(numbers, size) { it > 0 }
        val epsilon = simpleRate(numbers, size) { it < 0 }
        return gamma * epsilon
    }

    private fun advRate(numbers: List<Int>, size: Int, pick: (Int) -> Boolean): Int {
        return (1..size).fold(numbers) { candidates, i ->
            val mask = 1.shl(size - i)
            val bit = getBit(i, size, candidates, pick)

            val bitValToKeep = mask * bit
            if (candidates.size <= 1)
                candidates
            else
                candidates.filter { it.and(mask) == bitValToKeep }
        }.first()
    }

    fun part2(numbers: List<Int>, size: Int): Int {
        val oxyRating = advRate(numbers, size) { it >= 0 }
        val co2Rating = advRate(numbers, size) { it < 0 }

        return oxyRating * co2Rating
    }
}

fun main(args: Array<String>) {

    val lines = readLines("day3")

//    val lines = ("00100\n" +
//            "11110\n" +
//            "10110\n" +
//            "10111\n" +
//            "10101\n" +
//            "01111\n" +
//            "00111\n" +
//            "11100\n" +
//            "10000\n" +
//            "11001\n" +
//            "00010\n" +
//            "01010").split("\n")

    val size = lines[0].length
    val numbers = lines.map { it.toInt(2) }

    println("Part 1 = ${Day3.part1(numbers, size)} (693486)")
    println("Part 2 = ${Day3.part2(numbers, size)} (3379326)")
}