import java.io.File

fun readLines(day: String): List<String> {
    return File("input/${day}.txt").useLines { it.toList() }
}