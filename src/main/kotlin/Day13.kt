object Day13 {
    class Paper(private val dots: Set<Coord>) {
        fun mark(x: Int, y: Int) = Paper(dots + Coord(x, y))
        
        fun fold(fold: Fold): Paper {
            if (fold.at.y >= 0) {
                // fold up
                val folded = dots.filter { it.y > fold.at.y }.map { Coord(it.x, 2 * fold.at.y - it.y) }
                val newDots = dots.filter { it.y < fold.at.y } + folded
                return Paper(newDots.toSet())
            }
            // fold left
            val folded = dots.filter { it.x > fold.at.x }.map { Coord(2 * fold.at.x - it.x, it.y) }
            val newDots = dots.filter { it.x < fold.at.x } + folded
            return Paper(newDots.toSet())
        }

        fun count() = dots.size

        fun print() {
            val maxX = dots.maxOf { it.x }
            val maxY = dots.maxOf { it.y }
            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    val c = dots.contains(Coord(x, y))
                    print(if (c) "#" else ".")
                }
                println()
            }
        }
    }
    
    private fun emptyPaper() = Paper(emptySet<Coord>())
    
    class Fold(val at: Coord)

    private fun parse(lines: List<String>): Pair<Paper, List<Fold>> {
        val paper = lines.takeWhile { it != "" }.fold(emptyPaper()) { p, line ->
            val (x, y) = line.split(",")
            p.mark(x.toInt(), y.toInt())
        }
        val folds = lines.dropWhile { it != "" }.drop(1).fold(emptyList<Fold>()) { list, line ->
            val (_, _, s) = line.split(" ")
            val (c, v) = s.split("=")
            val x = if (c == "x") v.toInt() else -1
            val y = if (c == "y") v.toInt() else -1
            list + Fold(Coord(x, y))
        }
        return Pair(paper, folds)
    }

    fun part1(lines: List<String>): Int {
        val (paper, folds) = parse(lines)
        return paper.fold(folds.first()).count()
    }

    fun part2(lines: List<String>) {
        val (paper, folds) = parse(lines)
        val result = folds.fold(paper) { p, f -> p.fold(f) }
        result.print()
    }
}

fun main() {
    val testLines = readLines("day13_ex")
    val lines = readLines("day13")

    assert(17, Day13.part1(testLines), "Part 1, example")
    assert(693, Day13.part1(lines), "Part 1")

    Day13.part2(lines) // UCLZRAZU
}