import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ru.netology.PaymentSystem
import ru.netology.Transaction
import ru.netology.calculateCommission
import java.util.stream.Stream

class CalculateCommissionTest {
    companion object {
        @JvmStatic
        fun transactionProvider(): Stream<Arguments> = Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MIR, 22_000.0, 300_000.0, 140_000.0), 0.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VISA, 130_000.0, 590_000.0, 11_000.0), 0.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MASTERCARD, 0.0, 0.0, 140_000.0), 410.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MASTERCARD, 60_000.0, 85_000.0, 50_000.0), 320.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MASTERCARD, 20_000.0, 40_000.0, 60_000.0), 50.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MASTERCARD, 30_000.0, 70_000.0, 20_000.0), 110.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MASTERCARD, 10_000.0, 20_000.0, 30_000.0), 0.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VISA, 10_000.0, 20_000.0, 2_000.0), 35.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VISA, 10_000.0, 20_000.0, 15_000.0), 112.5
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MIR, 10_000.0, 20_000.0, 140_000.0), 1050.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VKPAY, 10_000.0, 20_000.0, 10_000.0), 0.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VKPAY, 10_000.0, 20_000.0, 45_000.0), 0.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VKPAY, 35_000.0, 38_000.0, 3_000.0), 0.0
            ),

            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.VKPAY, 39_000.0, 39_000.0, 3_000.0), 0.0
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.MAESTRO, 10_000.0, 20_000.0, 250.0), 21.5
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                Transaction(PaymentSystem.PACKMAN, 10_000.0, 20_000.0, 250.0), 0.0
            )
        )
    }

    @ParameterizedTest
    @MethodSource("transactionProvider")
    fun calculateCommissionTest(transaction: Transaction, expected: Double) {
        val result = calculateCommission(transaction)
        assertEquals(expected, result, 0.01) // 0.01 — допустимая погрешность для double
    }

    @Test
    fun calculateCommissionDefaultTest() {
        val result = calculateCommission()
        assertEquals(35.0, result) // 0.01 — допустимая погрешность для double
    }
}