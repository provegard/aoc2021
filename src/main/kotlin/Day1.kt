import java.io.File

fun part1(numbers: List<Int>): Int {
    return numbers.zipWithNext().count { it.second > it.first }
}

fun part2(numbers: List<Int>): Int {
    return numbers.windowed(3).zipWithNext().count { it.second.sum() > it.first.sum() }
}

fun main(args: Array<String>) {

    val numbers = File("input/day1.txt").useLines { it.toList() }.map { Integer.parseInt(it) }

    println("Part 1 = ${part1(numbers)}")
    println("Part 2 = ${part2(numbers)}")
}