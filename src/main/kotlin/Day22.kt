import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.math.abs

typealias Reactor = PersistentSet<Vector3D>

object Day22 {
    data class Instruction(val on: Boolean, val x: IntRange, val y: IntRange, val z: IntRange) {
        fun cubes() = sequence {
            x.forEach { xx ->
                y.forEach { yy ->
                    z.forEach { zz ->
                        yield(Vector3D(xx, yy, zz))
                    }
                }
            }
        }
    }

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
        return Instruction(on, xMin..xMax, yMin..yMax, zMin..zMax)
    }

    private fun parseLines(lines: List<String>) = lines.map(::parse)

    private fun isInit(v: Vector3D) = abs(v.x) <= 50 && abs(v.y) <= 50 && abs(v.z) <= 50
    private fun isInit(ins: Instruction) =
        ins.x.first >= -50 && ins.x.last <= 50 &&
        ins.y.first >= -50 && ins.y.last <= 50 &&
        ins.z.first >= -50 && ins.z.last <= 50

    fun part1(lines: List<String>): Int {
        val instructions = parseLines(lines)
        val initInstr = instructions.filter(::isInit)
        val result = initInstr.fold(persistentSetOf<Vector3D>()) { reactor, instr ->
            instr.cubes().fold(reactor) { r, c ->
                if (instr.on) r.add(c) else r.remove(c)
            }
        }
        return result.size
    }
}

fun main() {
    val testLines = readLines("day22_ex")
    val lines = readLines("day22")
    assert(590784, Day22.part1(testLines), "Part 1 test")
    assert(615700, Day22.part1(lines), "Part 1")
}