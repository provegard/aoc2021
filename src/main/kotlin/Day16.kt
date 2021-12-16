object Day16 {

    data class Packet(val version: Int, val typeId: Int, val value: ULong, val subPackets: List<Packet>) {

        fun flatten(): Sequence<Packet> {
            val self = this
            return sequence {
                yield(self)
                yieldAll(subPackets.flatMap { it.flatten() })
            }
        }

        fun calculate(): ULong {
            return when (typeId) {
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
    }

    private fun toBits(n: Int): List<String> {
        val bitRep = n.toString(2).padStart(4, '0')
        return bitRep.toCharArray().map { "$it"}
    }

    private fun hexToBits(input: String): List<String> = input.toCharArray().map { "$it".toInt(16) }.flatMap { toBits(it) }

    private fun valueOf(bits: List<String>) = bits.joinToString("").toInt(2)

    private fun <T>take2(list: List<T>, count: Int): Pair<List<T>, List<T>> {
        val a = list.take(count)
        val b = list.drop(count)
        return a to b
    }

    private fun parseLiteral(prefix: String, bits: List<String>): Pair<String, List<String>> {
        val (chunk, rest) = take2(bits, 5)
        val value = prefix + chunk.drop(1).joinToString("")
        if (chunk[0] == "0") {
            return value to rest
        }
        return parseLiteral(value, rest)
    }

    private fun parseLiteralPacket(version: Int, bits: List<String>): Pair<Packet, List<String>> {
        val (valueBitString, rest) = parseLiteral("", bits)
        val value = valueBitString.toULong(2)
        val pkg = Packet(version, 4, value, emptyList())
        return pkg to rest
    }

    private tailrec fun parsePackets(mem: List<Packet>, bits: List<String>, count: Int = Int.MAX_VALUE): Pair<List<Packet>, List<String>> {
        if (bits.isEmpty() || count == 0) return mem to bits
        val (pkg, rest) = parsePacket(bits)
        return parsePackets(mem + pkg, rest, count - 1)
    }

    private fun parsePackets(bits: List<String>, count: Int = Int.MAX_VALUE): Pair<List<Packet>, List<String>> {
        return parsePackets(emptyList(), bits, count)
    }

    private fun parseOperatorPacket(version: Int, typeId: Int, bits: List<String>): Pair<Packet, List<String>> {
        val (lengthTypeIdBits, rest) = take2(bits, 1)
        val lengthTypeId = lengthTypeIdBits.first()
        if (lengthTypeId == "0") {
            val (lenBits, rest2) = take2(rest, 15)
            val len = valueOf(lenBits)
            val (subBits, rest3) = take2(rest2, len)
            val (subPackets, _) = parsePackets(subBits)
            return Packet(version, typeId, 0UL, subPackets) to rest3
        }
        val (countBits, rest2) = take2(rest, 11)
        val count = valueOf(countBits)
        val (subPackets, rest3) = parsePackets(rest2, count)
        return Packet(version, typeId, 0UL, subPackets) to rest3
    }

    private fun parsePacket(bits: List<String>): Pair<Packet, List<String>> {
        val (verBits, rest) = take2(bits, 3)
        val (typeIdBits, data) = take2(rest, 3)
        val version = valueOf(verBits)
        val typeId = valueOf(typeIdBits)
        if (typeId == 4) {
            // Literal
            return parseLiteralPacket(version, data)
        }
        return parseOperatorPacket(version, typeId, data)
    }

    fun part1(input: String): Int {
        val (packet, _) = parsePacket(hexToBits(input))
        return packet.flatten().sumOf { it.version }
    }

    fun part2(input: String): ULong {
        val (packet, _) = parsePacket(hexToBits(input))
        return packet.calculate()
    }
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