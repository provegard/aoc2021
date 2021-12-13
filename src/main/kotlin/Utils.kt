import java.io.File

fun readLines(day: String): List<String> {
    return File("input/${day}.txt").useLines { it.toList() }
}

fun <T>assert(expected: T, actual: T, desc: String) {
    if (expected != actual) {
        throw RuntimeException("$desc: Expected $expected but got $actual")
    }
    println("$desc: OK!")
}

data class Pos(val r: Int, val c: Int)

data class Coord(val x: Int, val y: Int)