import kotlinx.collections.immutable.persistentListOf
import kotlin.system.measureTimeMillis

object Day24 {

    private fun program(next: () -> Long): Long {
        var w = 0L
        var x = 0L
        var y = 0L
        var z = 0L

        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 13
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 8
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 12
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 16
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 10
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 4
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + -11
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 1
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 14
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 13
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 13
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 5
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 12
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 0
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + -5
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 10
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 1
        x = x + 10
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 7
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + 0
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 2
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + -11
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 13
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + -13
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 15
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + -13
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y
        y = y * 0
        y = y + w
        y = y + 14
        y = y * x
        z = z + y
        w = next()
        x = x * 0
        x = x + z
        x = x % 26
        z = z / 26
        x = x + -11
        x = if (x == w) 1 else 0
        x = if (x == 0L) 1 else 0
        y = y * 0
        y = y + 25
        y = y * x
        y = y + 1
        z = z * y

        y = y * 0
        y = y + w
        y = y + 9
        y = y * x
        z = z + y

        return z
    }

    class UnexpectedValueException(val digit: Int, val expected: Int) : RuntimeException() {
        override fun fillInStackTrace(): Throwable = this
    }

    class ContinueException : RuntimeException() {
        override fun fillInStackTrace(): Throwable = this
    }

    private fun opt(get: (Int) -> Long): Long {
        var x: Long
        var rem: Long
        var slots: List<Long>

        val d1 = get(1)
        val d2 = get(2)
        val d3 = get(3)
        val d4 = get(4)


        slots = listOf(d1 + 8, d2 + 16)
        if (d3 - 7 != d4) {
            throw UnexpectedValueException(4, (d3 - 7).toInt())
        }

        val d5 = get(5)
        val d6 = get(6)

        slots = slots + listOf(d5 + 13, d6 + 5)

        val d7 = get(7)
        val d8 = get(8)
        rem = d6 + 5
        if (d7 - 5 != d8) {
            throw UnexpectedValueException(8, (d7 - 5).toInt())
        }

        val d9 = get(9)
        val d10 = get(10)
        if (d9 + 7 != d10) {
            throw UnexpectedValueException(10, (d9 + 7).toInt())
        }

        val d11 = get(11)
        x = rem - 11
        slots = slots.dropLast(1)
        if (x != d11) {
            throw UnexpectedValueException(11, x.toInt())
        } else rem = slots.last()

        val d12 = get(12)
        x = rem - 13
        slots = slots.dropLast(1)
        if (x != d12) {
            throw UnexpectedValueException(12, x.toInt())
        } else rem = slots.last()

        val d13 = get(13)
        x = rem - 13
        slots = slots.dropLast(1)
        if (x != d13) {
            throw UnexpectedValueException(13, x.toInt())
        } else rem = slots.last()

        val d14 = get(14)
        x = rem - 11
//        slots = slots.dropLast(1)
        if (x != d14) {
            throw UnexpectedValueException(14, x.toInt())
        }

        return 0L
    }

    private fun runProg(num: Long): Long {
        val digits = num.toString()
        var cur = 0
        return program { (digits[cur++].code - 48).toLong() }
    }

    private fun discover(mem: List<Int>, digit: Int, upper: Int, lower: Int, callback: (Long) -> Unit) {
        require(upper <= 9)
        require(lower >= 1)
        require(upper >= lower)
        if (mem.size == 14) {
            callback(mem.joinToString("").toLong())
        }

        for (n in upper downTo lower) {
            try {
                opt {
                    if (it > mem.size) {
                        discover(mem + n, digit + 1, 9, 1, callback)
                        throw ContinueException()
                    } else mem[it - 1].toLong()
                }
            } catch (e: UnexpectedValueException) {
                if (e.digit == digit) {
                    if (e.expected !in 1..9) {
                        break
                    }
                    return discover(mem, digit, e.expected, e.expected, callback)
                } else throw e
            } catch (e: ContinueException) {
            }
        }
    }

    private fun discover(callback: (Long) -> Unit) = discover(emptyList(), 1, 9, 1, callback)

    fun part(): Pair<Long, Long> {
        var result = persistentListOf<Long>()

        discover { result = result.add(it) }

        val valid = result.filter { runProg(it) == 0L }

        val mx = valid.maxOrNull() ?: throw RuntimeException("???")
        val mn = valid.minOrNull() ?: throw RuntimeException("???")
        return (mn to mx)
    }
}

fun main() {
    val ms = measureTimeMillis {
        val (mn, mx) = Day24.part()
        assert(96929994293996L, mx, "Part 1")
        assert(41811761181141L, mn, "Part 2")
    }
    println("Part 1+2 took $ms ms")
}