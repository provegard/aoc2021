import kotlin.math.max
import kotlin.math.min

object Day22 {
    fun IntRange.intersect(other: IntRange): IntRange? {
        if (other.first > this.last) return null
        if (other.last < this.first) return null
        return max(first, other.first)..min(last, other.last)
    }

    data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {
        fun count(): Long =
            (x.last - x.first + 1).toLong() *
            (y.last - y.first + 1).toLong() *
            (z.last - z.first + 1).toLong()

        fun intersect(other: Cuboid): Cuboid? {
            val xIsect = x.intersect(other.x) ?: return null
            val yIsect = y.intersect(other.y) ?: return null
            val zIsect = z.intersect(other.z) ?: return null
            return Cuboid(xIsect, yIsect, zIsect)
        }
    }

    class Reactor(private val deltas: List<Delta> = emptyList()) {

        private fun apply(delta: Delta): Reactor {
            // Add inverse compensations for intersections.
            // on-*    => add a negative compensation (to not double-count)
            // off-*   => add a positive compensation (to cancel a previous compensation)
            val compensations = deltas.mapNotNull { prev ->
                prev.cuboid.intersect(delta.cuboid)?.let { Delta(!prev.on, it) }
            }
            val toAdd = if (delta.on) {
                listOf(delta) + compensations
            } else compensations
            return Reactor(deltas + toAdd)
        }

        fun apply(ins: Instruction): Reactor = apply(Delta(ins.on, ins.cuboid))

        fun count(): Long {
            return deltas.fold(0L) { count, delta ->
                val cnt = delta.cuboid.count()
                count + (if (delta.on) cnt else -cnt)
            }
        }
    }

    data class Delta(val on: Boolean, val cuboid: Cuboid)

    data class Instruction(val on: Boolean, val cuboid: Cuboid)

    private fun parse(line: String): Instruction {
        // on x=-20..26,y=-36..17,z=-47..7
        val result = """(o(n|ff)) x=(([0-9-]+)[.][.]([0-9-]+)),y=(([0-9-]+)[.][.]([0-9-]+)),z=(([0-9-]+)[.][.]([0-9-]+))""".toRegex().matchEntire(line) ?: throw RuntimeException("No match for: $line")
        val values = result.groupValues
        val onOff = values[1]
        val xMin = values[4].toInt()
        val xMax = values[5].toInt()
        val yMin = values[7].toInt()
        val yMax = values[8].toInt()
        val zMin = values[10].toInt()
        val zMax = values[11].toInt()

        val on = onOff == "on"
        return Instruction(on, Cuboid(xMin..xMax, yMin..yMax, zMin..zMax))
    }

    private fun parseLines(lines: List<String>) = lines.map(::parse)

    private fun isInit(ins: Instruction) =
        ins.cuboid.x.first >= -50 && ins.cuboid.x.last <= 50 &&
        ins.cuboid.y.first >= -50 && ins.cuboid.y.last <= 50 &&
        ins.cuboid.z.first >= -50 && ins.cuboid.z.last <= 50

    private fun run(instructions: List<Instruction>): Long {
        return instructions.fold(Reactor()) { r, ins -> r.apply(ins) }.count()
    }

    fun part1(lines: List<String>): Long {
        val instructions = parseLines(lines)
        val initInstr = instructions.filter(::isInit)
        return run(initInstr)
    }

    fun part2(lines: List<String>): Long {
        val instructions = parseLines(lines)
        return run(instructions)
    }
}

fun main() {
    val testLines = readLines("day22_ex")
    val testLines2 = readLines("day22_ex2")
    val lines = readLines("day22")

    assert(8L, Day22.part1(listOf("on x=0..1,y=0..1,z=0..1")), "test 1")
    assert(16L, Day22.part1(listOf(
        "on x=0..1,y=0..1,z=0..1",
        "on x=2..3,y=2..3,z=2..3",
    )), "test 2")
    assert(7L, Day22.part1(listOf(
        "on x=0..1,y=0..1,z=0..1",
        "off x=1..2,y=1..2,z=1..2",
    )), "test 3")
    assert(8L + 8L - 1L, Day22.part1(listOf(
        "on x=0..1,y=0..1,z=0..1",
        "off x=1..2,y=1..2,z=1..2",
        "on x=1..2,y=1..2,z=1..2",
    )), "test 4")

    assert(590784L, Day22.part1(testLines), "Part 1 test")
    assert(615700L, Day22.part1(lines), "Part 1")
    assert(2758514936282235L, Day22.part2(testLines2), "Part 2, test")
    assert(1236463892941356L, Day22.part2(lines), "Part 2")
}