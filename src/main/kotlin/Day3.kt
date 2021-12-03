object Day3 {

    fun part1(numbers: List<Int>, size: Int): Int {
        var gamma = 0
        var epsilon = 0

        for (i in (size - 1) downTo 0) {
            val bitValue = 1.shl(i)

            val ones = numbers.count { it.and(bitValue) != 0 }
            val zeroes = numbers.size - ones

            val gammaBit = if (ones > zeroes) 1 else 0
            val epsilonBit = 1 - gammaBit

            gamma = (gamma shl 1) + gammaBit
            epsilon = (epsilon shl 1) + epsilonBit
        }

        return gamma * epsilon
    }

    private fun findRating(numbers: List<Int>, size: Int, keep: Int, pick: (Int, Int) -> Boolean): Int {
        var candidates = numbers

        while (candidates.size > 1) {
            for (i in (size - 1) downTo 0) {
                val bitValue = 1.shl(i)

                val ones = candidates.count { it.and(bitValue) != 0 }
                val zeroes = candidates.size - ones

                val crit = if (ones == zeroes) (bitValue * keep) else {
                    if (pick(ones, zeroes)) bitValue else 0
                }

                candidates = candidates.filter { it.and(bitValue) == crit }

                if (candidates.size <= 1) {
                    break
                }
            }
        }

        return candidates[0]
    }

    fun part2(numbers: List<Int>, size: Int): Int {
        val oxyRating = findRating(numbers, size, 1) { ones, zeroes -> ones > zeroes }
        val co2Rating = findRating(numbers, size, 0) { ones, zeroes -> ones < zeroes }

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

    println("Part 1 = ${Day3.part1(numbers, size)}")
    println("Part 2 = ${Day3.part2(numbers, size)}")
}