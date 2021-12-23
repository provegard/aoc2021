import kotlin.math.abs
import kotlin.math.min
import kotlin.system.measureTimeMillis

object Day23 {
    enum class SpaceType {
        CORRIDOR_NOT_OUTSIDE_ROOM, CORRIDOR_OUTSIDE_ROOM, ROOM
    }

    private val moveCost = mapOf(
        'A' to 1,
        'B' to 10,
        'C' to 100,
        'D' to 1000
    )
    private fun getMoveCost(amphipod: Char) = moveCost[amphipod] ?: throw RuntimeException("Unknown amphipod: $amphipod")

    private fun neighbors(c: Coord): List<Coord> {
        return listOf(
            Coord(c.x - 1, c.y),
            Coord(c.x + 1, c.y),
            Coord(c.x, c.y - 1),
            Coord(c.x, c.y + 1)
        )
    }
    private fun manhattan(c1: Coord, c2: Coord) = abs(c1.x - c2.x) + abs(c1.y - c2.y)

    data class Space(internal val type: SpaceType, internal val roomFor: Char?, val occupant: Char?, val locked: Boolean) {
        fun isOccupied() = occupant != null
        fun isHomeFor(amphipod: Char) = roomFor == amphipod
        fun isOccupiedByOther() = occupant != null && roomFor != null && occupant != roomFor
        fun isOccupiedByCorrect() = roomFor != null && occupant == roomFor

        fun isRoom() = type == SpaceType.ROOM
        fun isCorridorNotOutsideRoom() = type == SpaceType.CORRIDOR_NOT_OUTSIDE_ROOM

        fun occupy(amphipod: Char, lock: Boolean): Space {
            if (occupant != null) throw RuntimeException("$amphipod cannot occupy space already occupied by $occupant")
            return Space(type, roomFor, amphipod, lock)
        }

        fun leave(): Space {
            if (occupant == null) throw RuntimeException("Already empty")
            if (locked) throw RuntimeException("Locked amphipod")
            return Space(type, roomFor, null, false)
        }
    }

    fun shouldLock(spaces: Map<Coord, Space>, coord: Coord, space: Space, amphipod: Char): Boolean {
        if (!space.isHomeFor(amphipod)) return false
        val below = Coord(coord.x, coord.y + 1)
        // Lock if the space below is locked, or lock because there is no space below
        return spaces[below]?.locked ?: true
    }

    // Init operation, lock amphipods already in place
    private fun lockAsNeeded(spaces: Map<Coord, Space>): Map<Coord, Space> {
        return spaces.entries.sortedByDescending { it.key.y }.fold(emptyMap<Coord, Space>()) { mp, e ->
            val newEntry = if (e.value.occupant != null) {
                val lock = shouldLock(mp, e.key, e.value, e.value.occupant!!)
                e.key to Space(e.value.type, e.value.roomFor, e.value.occupant, lock)
            } else (e.key to e.value)
            mp + newEntry
        }.toMap()
    }

    data class Burrow(private val spaces: Map<Coord, Space>) {

        fun movableOccupants() = spaces.entries.filter {
            if (!it.value.isOccupied()) false else {
                !it.value.isRoom() || !it.value.locked
            }
        }

        fun dump(types: Boolean) {
            val maxX = spaces.keys.maxOf { it.x }
            val maxY = spaces.keys.maxOf { it.y }
            for (y in 0..maxY+1) {
                for(x in 0..maxX+1) {
                    val space = spaces[Coord(x, y)]
                    if (space == null) print('#')
                    else if (types) {
                        print(when (space.type) {
                            SpaceType.ROOM -> space.roomFor
                            SpaceType.CORRIDOR_OUTSIDE_ROOM -> 'x'
                            SpaceType.CORRIDOR_NOT_OUTSIDE_ROOM -> '.'
                        })
                    } else print(space.occupant ?: '.')
                }
                println()
            }
            println()
        }

        fun getPossibleDestinations(e: Map.Entry<Coord, Space>): List<Coord> {
            val space = e.value
            val amphipod = space.occupant ?: throw RuntimeException("Not occupied: ${e.key}")
            val unoccupied = spaces.entries.filter { !it.value.isOccupied() }

            val homeRooms = if (homeRoomTypeIsOccupiedByOther(amphipod)) emptyList()
            else unoccupied.filter { it.value.isHomeFor(amphipod) }.sortedByDescending { it.key.y }.take(1) // always go for the lowest room

            val entries = if (space.isRoom()) {
                // If it's a room, prefer moving into a home room, otherwise consider all corridor spaces not outside a room.
                homeRooms.ifEmpty { unoccupied.filter { it.value.isCorridorNotOutsideRoom() } }
            } else {
                // If it's not a room, then go for home rooms - don't stay in the corridor.
                homeRooms
            }
            return entries.map { it.key }
        }

        private fun homeRoomTypeIsOccupiedByOther(amphipod: Char): Boolean {
            return spaces.values.any { it.isHomeFor(amphipod) && it.isOccupiedByOther() }
        }

        fun getStepsToDest(from: Coord, to: Coord): Int? {
            // This is faster than A-star
            val visited = mutableSetOf<Coord>()
            var steps = 0
            var current = from
            while (current != to) {
                visited.add(current)
                val candidates = neighbors(current).filter { x -> spaces.contains(x) && !spaces[x]!!.isOccupied() && !visited.contains(x) }
                if (candidates.isEmpty()) return null
                val (next, _) = candidates.map { it to manhattan(it, to) }.minByOrNull { it.second }!!
                current = next
                steps++
            }
            return steps
        }

        fun moveOccupant(from: Map.Entry<Coord, Space>, to: Coord): Burrow {
            val space = from.value
            val amphipod = space.occupant ?: throw RuntimeException("Not occupied: $from")
            val toSpace = spaces[to] ?: throw RuntimeException("Unknown coord: $to")

            val newFrom = from.key to space.leave()
            val newTo = to to toSpace.occupy(amphipod, shouldLock(spaces, to, toSpace, amphipod))
            return Burrow(spaces.plus(listOf(newFrom, newTo)))
        }

        fun allInPlace() =
            spaces.values.filter { it.isRoom() }.all { it.isOccupiedByCorrect() }
    }

    private fun solve(b: Burrow, costSoFar: Int, best: Int): Int? {
        if (b.allInPlace()) return costSoFar

        val movable = b.movableOccupants()

        var currentBest = best

        for (m in movable) {
            val amphipod = m.value.occupant!!
            val moveCost = getMoveCost(amphipod)
            val destinations = b.getPossibleDestinations(m)
            val pairs = destinations.map { it to b.getStepsToDest(m.key, it) }
            for (pair in pairs) {
                val (dest, steps) = pair
                if (steps == null) continue
                val cost = steps * moveCost

                if (costSoFar + cost < currentBest) {

                    val newB = b.moveOccupant(m, dest)
                    val candidate = solve(newB, costSoFar + cost, currentBest)

                    if (candidate != null) {
                        currentBest = min(currentBest, candidate)
                    }
                }
            }
        }

        return if (currentBest < best) currentBest else null
    }

    private fun parse(lines: List<String>): Burrow {
        val res = lines.foldIndexed(emptyMap<Coord, Space>()) { y, m, line ->
            line.toCharArray().foldIndexed(m) { x, mm, ch ->
                val isOutside = ch == '#' || ch == ' '
                if (isOutside) mm else {
                    val isCorridor = ch == '.'
                    val isCorridorOutsideRoom = isCorridor && (x in 3..9 && x % 2 != 0)
                    val isRoom = !isCorridor
                    val roomFor = if (isRoom) (64 + (x - 1) / 2).toChar() else null
                    val occupant = if (isRoom) ch else null
                    val type = if (isCorridorOutsideRoom) SpaceType.CORRIDOR_OUTSIDE_ROOM else
                        (if (isCorridor) SpaceType.CORRIDOR_NOT_OUTSIDE_ROOM else SpaceType.ROOM)
                    val space = Space(type, roomFor, occupant, false)
                    val coord = Coord(x, y)
                    mm + (coord to space)
                }
            }
        }
        return Burrow(lockAsNeeded(res))
    }

    fun part(lines: List<String>): Int? {
        val burrow = parse(lines)
        return solve(burrow, 0, Int.MAX_VALUE)
    }
}

fun main() {
    val testLines = readLines("day23_ex")
    val testLines2 = readLines("day23_ex2")
    val lines = readLines("day23")
    val lines2 = readLines("day23_2")

    val ms1 = measureTimeMillis { assert(12521, Day23.part(testLines), "Part 1 test") }
    println("Part 1 test took $ms1 ms")

    val ms2 = measureTimeMillis { assert(11417, Day23.part(lines), "Part 1") }
    println("Part 1 took $ms2 ms")

    val ms3 = measureTimeMillis { assert(44169, Day23.part(testLines2), "Part 2 test") }
    println("Part 2 test took $ms3 ms")

    val ms4 = measureTimeMillis { assert(49529, Day23.part(lines2), "Part 2") }
    println("Part 2 took $ms4 ms")
}