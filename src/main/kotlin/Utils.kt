import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.math.abs

fun readLines(day: String): List<String> {
    return File("input/${day}.txt").useLines { it.toList() }
}

fun <T>assert(expected: T, actual: T, desc: String) {
    if (expected != actual) {
        val msg = "$desc:\nExpected $expected\nbut got  $actual"
        throw RuntimeException(msg)
    }
    println("$desc: OK!")
}

data class Pos(val r: Int, val c: Int)

data class Coord(val x: Int, val y: Int)
data class Vector3D(val x: Int, val y: Int, val z: Int) {
    operator fun minus(other: Vector3D) = Vector3D(x - other.x, y - other.y, z - other.z)
    operator fun plus(other: Vector3D) = Vector3D(x + other.x, y + other.y, z + other.z)
    operator fun unaryMinus() = Vector3D(-x, -y, -z)

    companion object {
        val zero = Vector3D(0, 0, 0)
    }

    fun manhattan(other: Vector3D) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
}

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

fun sign(a: Int): Int {
    return if (a < 0) -1 else (if (a > 0) 1 else 0)
}

fun <T> Sequence<T>.takeWhileInclusive(pred: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}

fun <A> Iterable<A>.pairs(): Sequence<Pair<A, A>> {
    val self = this
    return sequence {
        self.forEach { a ->
            self.forEach { b ->
                if (a != b) yield(a to b)
            }
        }
    }
}

fun <A> Iterable<A>.pairsOneSided(): Sequence<Pair<A, A>> {
    val self = this
    return sequence {
        self.forEachIndexed { ia, a ->
            self.forEachIndexed { ib, b ->
                if (ib > ia) yield(a to b)
            }
        }
    }
}