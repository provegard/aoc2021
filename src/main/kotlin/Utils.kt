import java.io.File
import java.util.*
import kotlin.Comparator

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

fun aStar(start: Coord, goal: Coord, h: (Coord, Coord) -> Int, d: (Coord, Coord) -> Int, neighbors: (Coord) -> List<Coord>): List<Coord> {
    val cameFrom = mutableMapOf<Coord, Coord>()
    val gScore = mutableMapOf(start to 0)
    val fScore = mutableMapOf(start to h(start, goal))

    val fComp = Comparator<Coord> { a, b ->
        val fa = fScore.getOrDefault(a, Int.MAX_VALUE)
        val fb = fScore.getOrDefault(b, Int.MAX_VALUE)
        fa - fb
    }
    val openSet = PriorityQueue(fComp)
    openSet.add(start)

    while (openSet.isNotEmpty()) {
        val current = openSet.peek()!!

        if (current == goal) {
            return generateSequence(current) { cameFrom[it] }.toList().reversed()
        }

        openSet.remove(current)
        for (neighbor in neighbors(current)) {
            val tentG = gScore.getOrDefault(current, Int.MAX_VALUE) + d(current, neighbor)
            if (tentG < gScore.getOrDefault(neighbor, Int.MAX_VALUE)) {
                cameFrom[neighbor] = current
                gScore[neighbor] = tentG
                fScore[neighbor] = tentG + h(current, neighbor)
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor)
                }
            }
        }
    }
    throw RuntimeException("Failed to reach end")
}

fun <T>Iterator<T>.take(n: Int): Sequence<T> {
    val iter = this
    return sequence {
        var left = n
        while (left > 0) {
            if (!iter.hasNext()) throw RuntimeException("End of iterator, could only take ${n - left} of $n")
            yield(iter.next())
            left--
        }
    }
}