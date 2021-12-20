import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object Rotation {
    private fun toRad(deg: Int) = deg.toDouble() * Math.PI / 180.0

    fun rotateAroundX(src: Vector3D, deg: Int): Vector3D {
        val rad = toRad(deg)
        val (x, y, z) = src
        return Vector3D(x, (cos(rad) * y - sin(rad) * z).roundToInt(), (sin(rad) * y + cos(rad) * z).roundToInt())
    }

    fun rotateAroundY(src: Vector3D, deg: Int): Vector3D {
        val rad = toRad(deg)
        val (x, y, z) = src
        return Vector3D((cos(rad) * x - sin(rad) * z).roundToInt(), y, (sin(rad) * x + cos(rad) * z).roundToInt())
    }

    fun rotateAroundZ(src: Vector3D, deg: Int): Vector3D {
        val rad = toRad(deg)
        val (x, y, z) = src
        return Vector3D((cos(rad) * x - sin(rad) * y).roundToInt(), (sin(rad) * x + cos(rad) * y).roundToInt(), z)
    }
}