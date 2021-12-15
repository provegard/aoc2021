object Day14 {

    private fun parse(lines: List<String>): Pair<String, Map<String, String>> {
        val template = lines.first()

        val rules = lines.drop(2).fold(emptyMap<String, String>()) { acc, line ->
            val (src, dst) = line.split(" -> ")
            acc + (src to dst)
        }

        return template to rules
    }

    private fun pairFrequencies(s: String): Map<String, Long> {
        return s.windowed(2).groupBy { it }.map { it.key to it.value.size.toLong() }.toMap()
    }

    private fun <K, V>entryPairs(m: Map<K, V>) = m.entries.map { it.key to it.value }

    private fun applyRules(freq: Map<String, Long>, rules: Map<String, String>): Map<String, Long> {
        val newFreq = freq.entries.fold(emptyMap<String, Long>()) { np, e ->
            val ins = rules[e.key]!!
            val newPair1 = e.key[0] + ins
            val newPair2 = ins + e.key[1]
            np + listOf(
                newPair1 to np.getOrDefault(newPair1, 0L) + e.value, // add first new pair
                newPair2 to np.getOrDefault(newPair2, 0L) + e.value, // add second new pair
            )
        }

        return newFreq
    }

    fun diff(lines: List<String>, steps: Int = 10): Long {
        val (template, rules) = parse(lines)
        val pairFreq = pairFrequencies(template)

        val finalFreq = (1..steps).fold(pairFreq) { f, i -> applyRules(f, rules) }

        // Take the first of each pair (don't double-count), meaning we must add in the last template char
        val entryPairs = entryPairs(finalFreq).plus(Pair("${template.last()}", 1L))
        val charFrequencies = entryPairs.map { it.first[0] to it.second }.groupBy { it.first }.map { it.key to it.value.sumOf { p -> p.second } }

        return charFrequencies.maxOf { it.second } - charFrequencies.minOf { it.second }
    }

}

fun main() {
    val testLines = readLines("day14_ex")
    val lines = readLines("day14")

    assert(1588, Day14.diff(testLines), "Day 14 part 1, example")
    assert(2988, Day14.diff(lines), "Day 14 part 1")
    assert(3572761917024L, Day14.diff(lines, 40), "Day 14 part 2")
}