import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.netology.calculateVisaMirCommission

class CalculateVisaMirCommissionTest {
    @Test
    fun calculateVisaMirCommissionMoreThenMin(){
        val amount = 15_000.0

        val result = calculateVisaMirCommission(amount)
        assertEquals(112.5, result)
    }
    @Test
    fun calculateVisaMirCommissionMin(){
        val amount = 1_000.0
        val result = calculateVisaMirCommission(amount)
        assertEquals(35.0, result)
    }
}