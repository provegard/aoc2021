
object Day14 {

    private fun parse(lines: List<String>): Pair<String, Map<String, String>> {
        val template = lines.first()

        val rules = lines.drop(2).fold(emptyMap<String, String>()) { acc, line ->
            val (src, dst) = line.split("[ ]+->[ ]+".toPattern())
            acc + (src to dst)
        }

        return template to rules
    }

    private fun applyRules(s: String, rules: Map<String, String>): String {
        val pairs = s.windowed(2)
        return pairs.joinToString("") {
            val ins = rules[it]
            if (ins != null) (it[0] + ins) else "${it[0]}"
        } + s.last()
    }

    private fun freqMap(s: String): Map<Char, Int> {
        return s.groupBy { it }.map { it.key to it.value.size }.toMap()
    }

    fun part1(lines: List<String>, steps: Int = 10): Int {
        val (template, rules) = parse(lines)
        val polymer = (1..steps).fold(template) { p, i -> applyRules(p, rules ) }
        val f = freqMap(polymer)
        val frequencies = f.values.sortedDescending()
        return frequencies.first() - frequencies.last()
    }
}

fun main() {
    val testLines = readLines("day14_ex")
    val lines = readLines("day14")

    assert(1588, Day14.part1(testLines), "Day 14 part 1, example")
    assert(2988, Day14.part1(lines), "Day 14 part 1")
}