object Day16 {

    data class Packet(val version: Int, val typeId: Int, val subPackets: List<Packet>) {
        fun visit(visitor: (Packet) -> Unit) {
            visitor(this)
            for (p in subPackets) p.visit(visitor)
        }
    }

    private fun toBits(n: Int): List<String> {
        val bitRep = n.toString(2).padStart(4, '0')
        return bitRep.toCharArray().map { "$it"}
    }

    private fun toBits(input: String): List<String> = input.toCharArray().map { "$it".toInt(16) }.flatMap { toBits(it) }

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
        val (value, rest) = parseLiteral("", bits)
        val pkg = Packet(version, 4, emptyList()) // TODO: Payload (value)
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
            return Packet(version, typeId, subPackets) to rest3
        }
        val (countBits, rest2) = take2(rest, 11)
        val count = valueOf(countBits)
        val (subPackets, rest3) = parsePackets(rest2, count)
        return Packet(version, typeId, subPackets) to rest3
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
        val (packet, _) = parsePacket(toBits(input))
        var verSum = 0
        packet.visit { verSum += it.version }
        return verSum
    }
}

fun main() {
    val lines = readLines("day16")

    assert(16, Day16.part1("8A004A801A8002F478"), "Part 1, example 1")
    assert(12, Day16.part1("620080001611562C8802118E34"), "Part 1, example 2")
    assert(23, Day16.part1("C0015000016115A2E0802F182340"), "Part 1, example 3")
    assert(31, Day16.part1("A0016C880162017C3686B18A3D4780"), "Part 1, example 4")
    assert(821, Day16.part1(lines.first()), "Part 1")
}