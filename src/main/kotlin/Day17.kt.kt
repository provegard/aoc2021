object Day17 {

    data class TargetArea(val xStart: Int, val xEnd: Int, val yStart: Int, val yEnd: Int)
    data class Positions(val coords: List<Coord>) {
        fun isNotEmpty() = coords.isNotEmpty()
        fun maxY() = coords.maxOf { it.y }
    }

    data class Probe(val pos: Coord, val velocity: Coord) {

        fun step(): Probe {
            val newX = pos.x + velocity.x
            val newY = pos.y + velocity.y
            val newVelX = velocity.x - sign(velocity.x)
            val newVelY = velocity.y - 1
            return Probe(Coord(newX, newY), Coord(newVelX, newVelY))
        }

        fun inTarget(area: TargetArea) =
            pos.x in area.xStart..area.xEnd && pos.y in area.yStart..area.yEnd

        fun passedTarget(area: TargetArea) =
            pos.x > area.xEnd || pos.y < area.yStart
    }

    private fun simulate(v: Coord, steps: Int, target: TargetArea): Positions {
        val probe = Probe(Coord(0, 0), v)
        val probes = generateSequence(probe) { it.step() }.take(steps).takeWhileInclusive { !it.inTarget(target) && !it.passedTarget(target) }.toList()
        return if (probes.last().inTarget(target)) Positions(probes.map { it.pos }) else Positions(emptyList())
    }


    private fun simulate(steps: Int, target: TargetArea): List<Pair<Coord, Positions>> {
        val velocities = (1..target.xEnd).flatMap { x -> (target.yStart..-target.yStart).map { y -> Coord(x, y) } }
        return velocities.map { it to simulate(it, steps, target) }.filter { it.second.isNotEmpty() }
    }

    private fun sim(area: TargetArea) = simulate(200, area)

    fun part1(area: TargetArea) = sim(area).maxOf { it.second.maxY() }

    fun part2(area: TargetArea) = sim(area).size
}


fun main() {
    // target area: x=288..330, y=-96..-50
    val area = Day17.TargetArea(288, 330, -96, -50)

    val testArea = Day17.TargetArea(20, 30, -10, -5)

    assert(45, Day17.part1(testArea), "Part 1 test")
    assert(4560, Day17.part1(area), "Part 1")

    assert(112, Day17.part2(testArea), "Part 2 test")
    assert(3344, Day17.part2(area), "Part 2")
}