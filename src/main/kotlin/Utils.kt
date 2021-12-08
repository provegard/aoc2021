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