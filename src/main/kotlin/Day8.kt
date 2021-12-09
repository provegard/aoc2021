object Day8 {

    private fun charsSet(s: String): Set<Char> = s.toCharArray().toSet()
    private val segChars = mapOf(
        0 to charsSet("abcefg"),
        1 to charsSet("cf"),
        2 to charsSet("acdeg"),
        3 to charsSet("acdfg"),
        4 to charsSet("bcdf"),
        5 to charsSet("abdfg"),
        6 to charsSet("abdefg"),
        7 to charsSet("acf"),
        8 to charsSet("abcdefg"),
        9 to charsSet("abcdfg")
    )

    private val segCount = segChars.map { it.key to it.value.size }.toMap()

    // digit A -> digits B1, B2... such that segments of Bn is a subset of segments of A
    private val containsByDigit = segChars.map {
        val others = segChars.entries.filter { e -> e.key != it.key && it.value.containsAll(e.value) }.map { e -> e.key }
        it.key to others
    }.toMap()

    fun part1(lines: List<String>): Int {
        val patternsPerLine = lines.map { it.split("|")[1].trim().split(" ") }
        val soughtDigits = listOf(1, 4, 7, 8)

        return patternsPerLine.sumOf { patterns ->
            soughtDigits.map { segCount[it] }.sumOf { segs -> patterns.count { it.length == segs } }
        }
    }

    private tailrec fun resolve(cc: Map<Int, List<Set<Char>>>): Map<Int, List<Set<Char>>> {
        val known = cc.filter { it.value.size == 1 }.map { it.key to it.value.first() }.toMap()
        if (known.size == 10) return cc // done

        val keep = cc.filter { it.value.size == 1 }
        val modified = cc.filter { it.value.size > 1 }.map { cur ->
            val newValue = cur.value
                // remove known
                .filterNot { p -> known.containsValue(p) }
                // then filter by known segments that this digit must contain
                .filter { p ->
                    val subSets = containsByDigit[cur.key]!!.mapNotNull { dd -> known[dd] }
                    subSets.all { s -> p.containsAll(s) }
                }
                // then filter by known segments that must contain this digit
                .filter { p ->
                    val superSets = containsByDigit.filter { e -> e.value.contains(cur.key) }.mapNotNull { e -> known[e.key] }
                    superSets.all { s -> s.containsAll(p) }
                }
            cur.key to newValue
        }.toMap()

        return resolve(keep + modified)
    }

    fun part2(lines: List<String>): Int {
        val patternsPerLine = lines.map {
            val parts = it.split("|").map { p -> p.trim().split(" ") }
            Pair(parts[0], parts[1])
        }
        return patternsPerLine.sumOf {
            val (before, after) = it
            val uniqPatterns = (before + after).map { p -> p.toSet() }.distinct()

            val initial = (0..9).associateWith { d -> uniqPatterns.filter { p -> p.size == segCount[d] } }.toMap()
            val resolved = resolve(initial)

            val nums = after.map { p ->
                resolved.entries.first { e ->
                    val chars = p.toCharArray().toList().toSet()
                    e.value.single() == chars
                }.key
            }
            nums.joinToString("").toInt()
        }
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
    assert(61229, Day8.part2(testLines), "part 2, test input")
    assert(994266, Day8.part2(lines), "part 2, real input")
}