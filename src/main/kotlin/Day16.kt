object Day16 {

    data class Packet(val version: Int, val typeId: Int, val value: ULong, val subPackets: List<Packet>) {

        fun flatten(): List<Packet> = listOf(this) + subPackets.flatMap { it.flatten() }

        fun calculate(): ULong = when (typeId) {
            0 -> subPackets.sumOf { it.calculate() }
            1 -> subPackets.fold(1UL) { acc, p -> acc * p.calculate() }
            2 -> subPackets.minOf { it.calculate() }
            3 -> subPackets.maxOf { it.calculate() }
            4 -> value
            5 -> if (subPackets[0].calculate() > subPackets[1].calculate()) 1UL else 0UL
            6 -> if (subPackets[0].calculate() < subPackets[1].calculate()) 1UL else 0UL
            7 -> if (subPackets[0].calculate() == subPackets[1].calculate()) 1UL else 0UL
            else -> throw RuntimeException("Unknown type ID: $typeId")
        }
    }

    private fun toBits(n: Int): List<String> {
        val bitRep = n.toString(2).padStart(4, '0')
        return bitRep.toCharArray().map { "$it"}
    }

    private fun hexToBitIter(input: String): Iterator<String> = input.toCharArray().map { "$it".toInt(16) }.flatMap { toBits(it) }.iterator()

    private fun valueOf(bits: List<String>) = bits.joinToString("").toULong(2)
    private fun valueOf(bits: Sequence<String>) = valueOf(bits.toList())

    private fun parseLiteral(prefix: List<String>, bits: Iterator<String>): List<String> {
        val chunk = bits.take(5).toList()
        val value = prefix + chunk.drop(1)
        return when (chunk.first()) {
            "0" -> value
            else -> parseLiteral(value, bits)
        }
    }

    private fun parseLiteralPacket(version: Int, bits: Iterator<String>): Packet {
        val valueBits = parseLiteral(emptyList(), bits)
        val value = valueOf(valueBits)
        return Packet(version, 4, value, emptyList())
    }

    private tailrec fun parsePackets(mem: List<Packet>, bits: Iterator<String>, count: Int = Int.MAX_VALUE): List<Packet> {
        if (!bits.hasNext() || count == 0) return mem
        val pkg = parsePacket(bits)
        return parsePackets(mem + pkg, bits, count - 1)
    }

    private fun parsePackets(bits: Iterator<String>, count: Int = Int.MAX_VALUE): List<Packet> = parsePackets(emptyList(), bits, count)

    private fun parseOperatorPacket(version: Int, typeId: Int, bits: Iterator<String>): Packet {
        val lengthTypeId = bits.take(1).first()
        val subPackets = if (lengthTypeId == "0") {
            val len = valueOf(bits.take(15)).toInt()
            val subBits = bits.take(len)
            parsePackets(subBits.iterator())
        } else {
            val count = valueOf(bits.take(11)).toInt()
            parsePackets(bits, count)
        }
        return Packet(version, typeId, 0UL, subPackets)
    }

    private fun parsePacket(bits: Iterator<String>): Packet {
        val version = valueOf(bits.take(3)).toInt()
        val typeId = valueOf(bits.take(3)).toInt()
        return if (typeId == 4)
            parseLiteralPacket(version, bits) else
            parseOperatorPacket(version, typeId, bits)
    }

    fun part1(input: String): Int = parsePacket(hexToBitIter(input)).flatten().sumOf { it.version }

    fun part2(input: String): ULong = parsePacket(hexToBitIter(input)).calculate()
}

fun main() {
    val lines = readLines("day16")

    assert(16, Day16.part1("8A004A801A8002F478"), "Part 1, example 1")
    assert(12, Day16.part1("620080001611562C8802118E34"), "Part 1, example 2")
    assert(23, Day16.part1("C0015000016115A2E0802F182340"), "Part 1, example 3")
    assert(31, Day16.part1("A0016C880162017C3686B18A3D4780"), "Part 1, example 4")
    assert(821, Day16.part1(lines.first()), "Part 1")
    assert(2056021084691UL, Day16.part2(lines.first()), "Part 2")
}