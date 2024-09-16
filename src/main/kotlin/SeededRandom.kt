import java.util.*
import kotlin.random.Random

object SeededRandom {

  private var random = Random(0)

  fun reset(seed: Long = 0) {
    random = Random(seed)
  }

  fun uuid(): UUID {
    return UUID(
      random.nextLong().toULong()
        .and(0xffffffffffff0fffUL).or(0x4000UL).toLong(),
      random.nextLong().toULong()
        .and(0x0fffffffffffffffUL).or(0x8000000000000000UL).toLong()
    )
  }

  fun bytes(length: Int): ByteArray {
    return ByteArray(length).also { random.nextBytes(it) }
  }

  fun string(from: Int = 5, until: Int = 15, characterSet: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"): String {
    val length = random.nextInt(from, until)
    return (1..length)
      .map { characterSet.random(random) }
      .joinToString("")
  }

  fun long(lower: Long = Long.MIN_VALUE, upper: Long = Long.MAX_VALUE): Long {
    return random.nextLong(lower, upper)
  }

  fun int(lower: Int = Int.MIN_VALUE, upper: Int = Int.MAX_VALUE): Int {
    return random.nextInt(lower, upper)
  }

  fun float(lower: Float = Float.MIN_VALUE, upper: Float = Float.MAX_VALUE): Float {
    val diff = upper - lower
    return lower + (random.nextFloat() * diff)
  }
}
