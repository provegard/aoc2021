import kotlin.math.roundToInt

object Day19 {
    data class Beacon(val id: String, val vec: Vector3D)

    class Scanner(val num: Int, val beacons: List<Beacon>, val pos: Vector3D?)

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
            val id = "$num-$idx"
            acc + Beacon(id, Vector3D(x.toInt(), y.toInt(), z.toInt()))
        }
        val scanner = Scanner(num, beacons, if (num == 0) Vector3D.zero else null)
        return parse(lines.drop(l.size + 1), collected + scanner)
    }

    private fun Vector3D.rotXYZ(rot: Vector3D): Vector3D {
        val vd = Rotation.Vec3DD(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
        val result = vd.rotX(rot.x).rotY(rot.y).rotZ(rot.z)
        return Vector3D(result.x.roundToInt(), result.y.roundToInt(), result.z.roundToInt())
    }

    private fun syncSecondScanner(s1: Scanner, s2: Scanner): Scanner? {
        assert(s1.pos != null)

        val s1PairsByDiff = s1.beacons.pairs().associateBy { it.second.vec - it.first.vec }

        val match = rotations
            .map { rot ->
                val s2BeaconsRotated = s2.beacons.map { Beacon(it.id, it.vec.rotXYZ(rot)) }
                val s2PairsByDiff = s2BeaconsRotated.pairs().associateBy { it.second.vec - it.first.vec }

                val matchingPairs = s2PairsByDiff.entries.filter { s1PairsByDiff.containsKey(it.key) }
                val uniqId = matchingPairs.toList().flatMap { p -> listOf(p.value.first.id, p.value.second.id) }.distinct()

                val diff = matchingPairs.firstOrNull()?.let {
                    val v1 = s1PairsByDiff[it.key]!!.first.vec
                    val v2 = s2PairsByDiff[it.key]!!.first.vec
                    v1 - v2
                } ?: Vector3D.zero

                val newBeacons = s2BeaconsRotated.map { b -> Beacon(b.id, b.vec + diff) }
                Triple(uniqId, newBeacons, diff)
            }
            .filter { it.first.size >= 12 }
            .maxByOrNull { it.first.size }

        return match?.let { Scanner(s2.num, it.second, it.third) }
    }

    private tailrec fun lockAll(locked: List<Scanner>, rest: List<Scanner>): List<Scanner> {
        if (rest.isEmpty()) return locked
        val newLocked = rest.flatMap { r -> locked.mapNotNull { syncSecondScanner(it, r) } }
        val newLockedNums = newLocked.map { it.num }.toSet()

        val newRest = rest.filter { !newLockedNums.contains(it.num) }
        return lockAll(locked + newLocked, newRest)
    }

    fun part1(lines: List<String>): Int {
        val scanners = parse(lines)
        val locked = lockAll(scanners.take(1), scanners.drop(1))
        val beacons = locked.flatMap { it.beacons }.distinctBy { it.vec }
        return beacons.size
    }

    fun scannerPositions(lines: List<String>): Map<Int, Vector3D> {
        val scanners = parse(lines)
        val locked = lockAll(scanners.take(1), scanners.drop(1))
        return locked.associate { it.num to it.pos!! }
    }

    fun part2(lines: List<String>): Int {
        val scanners = parse(lines)
        val locked = lockAll(scanners.take(1), scanners.drop(1))
        return locked.pairsOneSided().maxOf { it.first.pos!!.manhattan(it.second.pos!!) }
    }
}

fun main() {
    val testLines = readLines("day19_ex")
    val lines = readLines("day19")

    assert(79, Day19.part1(testLines), "Part 1, test")
    assert(365, Day19.part1(lines), "Part 1")

    val scannerPosTest = Day19.scannerPositions(testLines)
    assert(Vector3D(0, 0, 0), scannerPosTest[0], "Scanner 0 pos, test")
    assert(Vector3D(68, -1246, -43), scannerPosTest[1], "Scanner 1 pos, test")
    assert(Vector3D(-92, -2380, -20), scannerPosTest[3], "Scanner 3 pos, test")
    assert(Vector3D(-20, -1133, 1061), scannerPosTest[4], "Scanner 4 pos, test")
    assert(Vector3D(1105, -1205, 1229), scannerPosTest[2], "Scanner 2 pos, test")

    assert(3621, Day19.part2(testLines), "Part 2, test")
    assert(11060, Day19.part2(lines), "Part 2")
}