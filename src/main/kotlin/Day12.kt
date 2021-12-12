object Day12 {
    data class Node(val name: String) {
        fun isStart() = name == "start"
        fun isEnd() = name == "end"
        fun isSmall() = name == name.lowercase()
    }

    data class Path(val nodes: List<Node>)

    class Graph(private val connections: Map<Node, List<Node>>) {
        fun connect(a: Node, b: Node): Graph {
            val listA = connections.getOrDefault(a, emptyList())
            val listB = connections.getOrDefault(b, emptyList())
            val newConnections = connections + listOf(
                a to listA.plus(b),
                b to listB.plus(a)
            )
            return Graph(newConnections)
        }

        private fun walk(pathSoFar: List<Node>, node: Node): List<Path> {
            val newPath = pathSoFar + node
            if (node.isEnd()) return listOf(Path(newPath))

            val next = connections.getOrDefault(node, emptyList()).filter { !newPath.contains(it) || !it.isSmall() }
            return next.flatMap { walk(newPath, it) }
        }

        fun walk(): List<Path> {
            val start = connections.keys.first { it.isStart() }
            return walk(emptyList(), start)
        }
    }

    private fun toGraph(lines: List<String>): Graph {
        return lines.fold(Graph(emptyMap())) { g, line ->
            val parts = line.split("-")
            val n1 = Node(parts[0])
            val n2 = Node(parts[1])
            g.connect(n1, n2)
        }
    }

    fun part1(lines: List<String>): Int {
        return toGraph(lines).walk().size
    }
}

fun main() {
    assert(10, Day12.part1(readLines("day12_ex1")), "Part 1, example 1")
    assert(19, Day12.part1(readLines("day12_ex2")), "Part 1, example 2")
    assert(226, Day12.part1(readLines("day12_ex3")), "Part 1, example 3")
    assert(5920, Day12.part1(readLines("day12")), "Part 1")
}