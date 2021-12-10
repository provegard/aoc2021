object Day10 {

    private val match = mapOf(
        '(' to ')',
        '{' to '}',
        '[' to ']',
        '<' to '>'
    )
    private val syntaxErrorScore = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )
    private val autoCompleteScore = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )
    private fun isOpen(ch: Char): Boolean = match.keys.contains(ch)

    private tailrec fun findErr(remaining: List<Char>, memory: List<Char>): Char? {
        if (remaining.isEmpty()) return null
        val next = remaining.first()
        val rest = remaining.drop(1)
        if (isOpen(next)) {
            return findErr(rest, memory + next)
        }
        // close
        val last = memory.last()
        if (match[last] != next) {
            // syntax error
            return next
        }
        return findErr(rest, memory.dropLast(1))
    }

    private tailrec fun autoComplete(remaining: List<Char>, memory: List<Char>): String {
        if (remaining.isEmpty()) {
            return memory.reversed().map { match[it] }.joinToString("")
        }
        val next = remaining.first()
        val rest = remaining.drop(1)
        if (isOpen(next)) {
            return autoComplete(rest, memory + next)
        }
        return autoComplete(rest, memory.dropLast(1))
    }

    private fun findErr(line: String): Char? = findErr(line.toCharArray().toList(), emptyList())

    private fun isCorrupted(line: String): Boolean = findErr(line) != null

    private fun autoComplete(line: String): String = autoComplete(line.toCharArray().toList(), emptyList())

    fun part1(lines: List<String>): Int {
        return lines.sumOf {
            val err = findErr(it)
            syntaxErrorScore[err] ?: 0
        }
    }

    fun part2(lines: List<String>): Long {
        val scores = lines.filterNot { isCorrupted(it) }.map {
            val completion = autoComplete(it)
            completion.fold(0L) { score, ch -> score * 5L + (autoCompleteScore[ch] ?: 0) }
        }
        println(scores)
        return scores.sorted()[scores.size / 2]
    }

}

fun main() {
    val testLines = ("[({(<(())[]>[[{[]{<()<>>\n" +
            "[(()[<>])]({[<{<<[]>>(\n" +
            "{([(<{}[<>[]}>{[]{[(<()>\n" +
            "(((({<>}<{<{<>}{[]{[]{}\n" +
            "[[<[([]))<([[{}[[()]]]\n" +
            "[{[{({}]{}}([{[{{{}}([]\n" +
            "{<[[]]>}<{[{[{[]{()[[[]\n" +
            "[<(<(<(<{}))><([]([]()\n" +
            "<{([([[(<>()){}]>(<<{{\n" +
            "<{([{{}}[<[[[<>{}]]]>[]]").split("\n")
    val lines = readLines("day10")

    assert(26397, Day10.part1(testLines), "Part 1, test")
    assert(316851, Day10.part1(lines), "Part 1")
    assert(288957, Day10.part2(testLines), "Part 2, test")
    assert(2182912364L, Day10.part2(lines), "Part 2")
}