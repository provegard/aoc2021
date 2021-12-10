object Day10 {

    private val match = mapOf(
        '(' to ')',
        '{' to '}',
        '[' to ']',
        '<' to '>'
    )
    private val score = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )
    private fun isOpen(ch: Char): Boolean = match.keys.contains(ch)

    private fun findErr(remaining: List<Char>, memory: List<Char>): Char? {
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

    fun part1(lines: List<String>): Int {
        return lines.sumOf {
            val chars = it.toCharArray().toList()
            val err = findErr(chars, emptyList())
            score[err] ?: 0
        }
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
}