typealias Image = Map<Pos, Boolean>

object Day20 {
    
    private fun parse(lines: List<String>): Pair<String, Image> {
        val algo = lines.first()

        val image = lines.drop(1).foldIndexed(emptyMap<Pos, Boolean>()) { r, img, line ->
            line.toCharArray().foldIndexed(img) { c, imgg, ch ->
                val lit = ch == '#'
                imgg + (Pos(r, c) to lit)
            }
        }

        return algo to image
    }

    private fun dump(image: Image, algo: String, iteration: Int) {
        val margin = 3
        val minR = image.keys.minOf { it.r } - margin
        val minC = image.keys.minOf { it.c } - margin
        val maxR = image.keys.maxOf { it.r } + margin
        val maxC = image.keys.maxOf { it.c } + margin

        println(iteration)
        for (r in (minR..maxR)) {
            for (c in minC..maxC) {
                val pos = Pos(r, c)
                val infLit = algo[0] == '#' && iteration % 2 == 1
                val ch = when (image[pos]) {
                    true -> '#'
                    false -> '.'
                    else -> if (infLit) '#' else '.'
                }
                print(ch)
            }
            println()
        }
        println()
    }

    private fun win(pos: Pos): List<Pos> {
        return (-1..1).flatMap { r -> (-1..1).map { c -> Pair(r, c) } }
            .map { Pos(pos.r + it.first, pos.c + it.second) }
    }

    private fun isLit(algo: String, image: Image, at: Pos, iteration: Int): Boolean {
        val infLit = algo[0] == '#' && iteration % 2 == 1
        if (!image.containsKey(at)) return infLit
        return image[at] == true
    }

    private fun newPixLit(algo: String, image: Image, at: Pos, iteration: Int): Boolean {
        val idx = win(at).map { if (isLit(algo, image, it, iteration)) '1' else '0' }.joinToString("").toInt(2)
        return algo[idx] == '#'
    }

    private fun enhance(algo: String, image: Image, iteration: Int): Image {
        val minR = image.keys.minOf { it.r }
        val minC = image.keys.minOf { it.c }
        val maxR = image.keys.maxOf { it.r }
        val maxC = image.keys.maxOf { it.c }

        val margin = 1
        val fromR = minR - margin
        val fromC = minC - margin
        val toR = maxR + margin
        val toC = maxC + margin

        val newPixelPositions = (fromR..toR).flatMap { r -> (fromC..toC).map { c -> Pos(r, c) } }
        val newPixels = newPixelPositions.map { it to newPixLit(algo, image, it, iteration) }
        return newPixels.toMap()
    }

    private fun enhanceSeq(algo: String, image: Image): Sequence<Pair<Image, Int>> =
        generateSequence(image to 0) { enhance(algo, it.first, it.second) to (it.second + 1) }

    fun enhanceMain(lines: List<String>, iter: Int = 2): Int {
        val (algo, image) = parse(lines)
        val images = enhanceSeq(algo, image).take(1 + iter).toList()
//        for (idx in images.indices) {
//            dump(images[idx].first, algo, idx)
//        }
        return images.last().first.count { it.value }
    }
}

fun main() {
    val testLines = readLines("day20_ex")
    val lines = readLines("day20")

    assert(35, Day20.enhanceMain(testLines), "Day 20 part 1, example")
    assert(5437, Day20.enhanceMain(lines), "Day 20 part 1")
    assert(3351, Day20.enhanceMain(testLines, 50), "Day 20 part 2, example")
    assert(19340, Day20.enhanceMain(lines, 50), "Day 20 part 2")
}