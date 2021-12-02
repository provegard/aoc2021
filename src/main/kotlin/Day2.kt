import java.io.File

fun d2_part1(commands: List<Command>): Int {

    val (p, d) = commands.fold(Pair(0, 0)) { pair, cmd ->
        val (p, d) = pair

        when (cmd.op) {
            "forward" -> Pair(p + cmd.units, d)
            "up" -> Pair(p, d - cmd.units)
            "down" -> Pair(p, d + cmd.units)
            else -> throw Exception("Unknown cmd: ${cmd.op}")
        }
    }

    return p * d
}

fun d2_part2(commands: List<Command>): Int {

    val (p, d, _) = commands.fold(Triple(0, 0, 0)) { triple, cmd ->
        val (p, d, a) = triple

        when (cmd.op) {
            "forward" -> Triple(p + cmd.units, d + a * cmd.units, a)
            "up" -> Triple(p, d, a - cmd.units)
            "down" -> Triple(p, d, a + cmd.units)
            else -> throw Exception("Unknown cmd: ${cmd.op}")
        }
    }

    return p * d
}
data class Command(val op: String, val units: Int)

fun main(args: Array<String>) {

    val commands = File("input/day2.txt").useLines { it.toList() }.map {
        val parts = it.split(" ")
        val op = parts[0]
        val units = Integer.parseInt(parts[1])
        Command(op, units)
    }

    println("Part 1 = ${d2_part1(commands)}")
    println("Part 2 = ${d2_part2(commands)}")
}