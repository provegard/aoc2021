import kotlin.math.cos
import kotlin.math.sin

object Rotation {
    data class Vec3DD(val x: Double, val y: Double, val z: Double) {
        fun rotX(deg: Int) = rotateAroundX(this, deg)
        fun rotY(deg: Int) = rotateAroundY(this, deg)
        fun rotZ(deg: Int) = rotateAroundZ(this, deg)
    }
    
    private fun toRad(deg: Int) = deg.toDouble() * Math.PI / 180.0

    fun rotateAroundX(src: Vec3DD, deg: Int): Vec3DD {
        val rad = toRad(deg)
        val (x, y, z) = src
        return Vec3DD(x, (cos(rad) * y - sin(rad) * z), (sin(rad) * y + cos(rad) * z))
    }

    fun rotateAroundY(src: Vec3DD, deg: Int): Vec3DD {
        val rad = toRad(deg)
        val (x, y, z) = src
        return Vec3DD((cos(rad) * x - sin(rad) * z), y, (sin(rad) * x + cos(rad) * z))
    }

    fun rotateAroundZ(src: Vec3DD, deg: Int): Vec3DD {
        val rad = toRad(deg)
        val (x, y, z) = src
        return Vec3DD((cos(rad) * x - sin(rad) * y), (sin(rad) * x + cos(rad) * y), z)
    }
}