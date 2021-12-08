object Day8 {

    val segCount = mapOf(
        0 to 6,
        1 to 2,
        2 to 5,
        3 to 5,
        4 to 4,
        5 to 5,
        6 to 6,
        7 to 3,
        8 to 7,
        9 to 6
    )

    fun part1(lines: List<String>): Int {
        val patternsPerLine = lines.map { it.split("|")[1].trim().split(" ") }
        val soughtDigits = listOf(1, 4, 7, 8)

        return patternsPerLine.map { patterns ->
            soughtDigits.map { segCount[it] }.sumBy { segs -> patterns.count { it.length == segs } }
        }.sum()
    }
}

fun main() {
    val testLines = ("be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb |\n" +
            "fdgacbe cefdb cefbgd gcbe\n" +
            "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec |\n" +
            "fcgedb cgb dgebacf gc\n" +
            "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef |\n" +
            "cg cg fdcagb cbg\n" +
            "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega |\n" +
            "efabcd cedba gadfec cb\n" +
            "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga |\n" +
            "gecf egdcabf bgf bfgea\n" +
            "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf |\n" +
            "gebdcfa ecba ca fadegcb\n" +
            "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf |\n" +
            "cefg dcbef fcge gbcadfe\n" +
            "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd |\n" +
            "ed bcgafe cdgba cbgef\n" +
            "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg |\n" +
            "gbdfcae bgc cg cgb\n" +
            "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc |\n" +
            "fgae cfgab fg bagce").replace("|\n", "|")
        .split("\n")
    val lines = readLines("day8")

    assert(26, Day8.part1(testLines), "part 1, test input")
    assert(543, Day8.part1(lines), "part 1, real input")
}