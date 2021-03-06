import kotlin.system.measureTimeMillis

typealias Beacon = Vector3D

object Day19 {

    class Scanner(val num: Int, val beacons: List<Beacon>, val pos: Vector3D)

    private val rotations = sequence {
        (0..3).forEach { x ->
            (0..3).forEach { y ->
                (0..3).forEach { z ->
                    yield(Vector3D(90 * x, 90 * y, 90 * z))
                }
            }
        }
    }.distinct().toList()

    private tailrec fun parse(lines: List<String>, collected: List<Scanner> = emptyList()): List<Scanner> {
        if (lines.isEmpty()) return collected
        val l = lines.takeWhile { it != "" }
        val num = """\d+""".toRegex().find(l.first())!!.value.toInt()
        val beacons = l.drop(1).foldIndexed(emptyList<Beacon>()) { idx, acc, bl ->
            val (x, y, z) = bl.split(",")
            acc + Beacon(x.toInt(), y.toInt(), z.toInt())
        }
        val scanner = Scanner(num, beacons, Vector3D.zero)
        return parse(lines.drop(l.size + 1), collected + scanner)
    }

    private fun syncSecondScanner(s1: Scanner, s2: Scanner): Scanner? {
        val s1PairsByDiff = s1.beacons.pairs().associateBy { it.second - it.first }

        val match = rotations
            .map { rot ->
                val s2BeaconsRotated = s2.beacons.map { it.rotXYZ(rot) }
                val s2PairsByDiff = s2BeaconsRotated.pairs().associateBy { it.second - it.first }

                val matchingPairs = s2PairsByDiff.entries.filter { s1PairsByDiff.containsKey(it.key) }
                val uniqId = matchingPairs.flatMap { p -> listOf(p.value.first, p.value.second) }.distinct()

                val diff = matchingPairs.firstOrNull()?.let {
                    val v1 = s1PairsByDiff[it.key]!!.first
                    val v2 = s2PairsByDiff[it.key]!!.first
                    v1 - v2
                } ?: Vector3D.zero

                val newBeacons = s2BeaconsRotated.map { b -> b + diff }
                Triple(uniqId, newBeacons, diff)
            }
            .filter { it.first.size >= 12 }
            .maxByOrNull { it.first.size }

        return match?.let { Scanner(s2.num, it.second, it.third) }
    }

    private tailrec fun lockAll(locked: List<Scanner>, rest: List<Scanner>): List<Scanner> {
        if (rest.isEmpty()) return locked

        val newLocked = rest.flatMap { r -> listOfNotNull(locked.firstNotNullOfOrNull { syncSecondScanner(it, r) }) }
        val newLockedNums = newLocked.map { it.num }.toSet()

        val newRest = rest.filter { !newLockedNums.contains(it.num) }
        return lockAll(locked + newLocked, newRest)
    }

    private fun parseAndLock(lines: List<String>): List<Scanner> {
        val scanners = parse(lines)
        return lockAll(scanners.take(1), scanners.drop(1))
    }

    fun part1(lines: List<String>): Int {
        val locked = parseAndLock(lines)
        val uniqPoints = locked.flatMap { it.beacons }.distinct()
        return uniqPoints.size
    }
    fun part2(lines: List<String>): Int {
        val locked = parseAndLock(lines)
        return locked.pairsOneSided().maxOf { it.first.pos.manhattan(it.second.pos) }
    }
}

fun main() {
    val testLines = readLines("day19_ex")
    val lines = readLines("day19")

    assert(79, Day19.part1(testLines), "Part 1, test")
    val ms1 = measureTimeMillis {
        assert(365, Day19.part1(lines), "Part 1")
    }
    println("Part 1 took $ms1 ms")

    assert(3621, Day19.part2(testLines), "Part 2, test")
    assert(11060, Day19.part2(lines), "Part 2")
}