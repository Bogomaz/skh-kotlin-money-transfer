package ru.netology

import kotlin.Double

const val MAX_DAILY_LIMIT = 150_000.0
const val MAX_MONTHLY_LIMIT = 600_000.0

const val MAX_VK_TRANSACTION_LIMIT = 15_000.0
const val MAX_VK_MONTHLY_LIMIT = 40_000.0

const val MAX_MAESTRO_MASTERCARD_FREE_LIMIT = 75_000.0
const val MIN_MAESTRO_MASTERCARD_FREE_LIMIT = 300.0

const val MAESTRO_MASTERCARD_COMMISSION_RATE = 0.006
const val MAESTRO_MASTERCARD_FIXED_FEE = 20.0

const val VISA_MIR_COMMISSION_RATE = 0.0075
const val MIN_VISA_MIR_COMMISSION = 35.0

// Перечисление для платёжных систем
enum class PaymentSystem {
    MASTERCARD,
    MAESTRO,
    MIR,
    VISA,
    VKPAY,
    PACKMAN
}

// Типы превышения лимитов
enum class LimitExceedType {
    TRANSACTION,
    DAILY,
    MONTHLY
}


// Дата-класс, в котором будут храниться все параметры,
// необходимые для функции, вычисляющей попадание в лимиты
data class Transaction(
    val paymentSystem: PaymentSystem,
    val amountPerDay: Double,
    val amountPerMonth: Double,
    val amount: Double
)

// Создаём несколько переменных для проверки

fun main() {

    // проверяем работу ru.netology.calculateCommission() с параметром по умолчанию
    println("This is a commission calculator.")
}


// вычисление комиссии
fun calculateCommission(
    transaction: Transaction = Transaction(
        paymentSystem = PaymentSystem.MIR,
        amountPerDay = 0.0,
        amountPerMonth = 0.0,
        amount = 1.0
    )
): Double {
    // Если один из лимитов превышен, то перевод не произойдёт, следовательно комиссии не будет.
    // Во время проверки функция isLimitsExceeded выведет соответствующие сообщения в консоль.
    if (isLimitExceeded(transaction)) {
        return 0.0
    }

    return when (transaction.paymentSystem) {
        PaymentSystem.VISA, PaymentSystem.MIR ->
            calculateVisaMirCommission(transaction.amount)

        PaymentSystem.MASTERCARD, PaymentSystem.MAESTRO ->
            calculateMaestroMastercardCommission(transaction)

        PaymentSystem.VKPAY -> 0.0
        else -> {
            println("Неизвестный тип карты.")
            0.0
        }
    }
}

// Функция вычисляет комиссию для карт Visa или Мир
fun calculateVisaMirCommission(amount: Double): Double {
    val commission = amount * VISA_MIR_COMMISSION_RATE
    return if (commission > MIN_VISA_MIR_COMMISSION)
        commission
    else MIN_VISA_MIR_COMMISSION
}

// Функция вычисляет комиссию для Mastercard или Maestro
fun calculateMaestroMastercardCommission(transaction: Transaction): Double {

    val totalDay = transaction.amountPerDay + transaction.amount
    val totalMonth = transaction.amountPerMonth + transaction.amount

    return when {
        // 0. Платёж меньше установленной суммы
        transaction.amount < MIN_MAESTRO_MASTERCARD_FREE_LIMIT ->
            transaction.amount * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE
        // 1. Предыдущие платежи уже превысили лимит
        transaction.amountPerDay >= MAX_MAESTRO_MASTERCARD_FREE_LIMIT || transaction.amountPerMonth >= MAX_MAESTRO_MASTERCARD_FREE_LIMIT ->
            transaction.amount * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE

        // 2. Текущий платёж + сумма за день превышает лимит
        totalDay > MAX_MAESTRO_MASTERCARD_FREE_LIMIT ->
            (totalDay - MAX_MAESTRO_MASTERCARD_FREE_LIMIT) * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE

        // 3. Текущий платёж + сумма за месяц превышает лимит
        totalMonth > MAX_MAESTRO_MASTERCARD_FREE_LIMIT ->
            (totalMonth - MAX_MAESTRO_MASTERCARD_FREE_LIMIT) * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE

        // Остальные случаи — комиссия не взимается
        else -> 0.0
    }
}

// Функция проверяет дневной и месячный лимиты и выводит соответствующие сообщения.
// Если какой-то из лимитов превышен - вернёт true, если перевод укладывается в лимиты - false
fun isLimitExceeded(transaction: Transaction): Boolean {
    val totalDay = transaction.amountPerDay + transaction.amount
    val totalMonth = transaction.amountPerMonth + transaction.amount
    return when {
        // Проверяем дневной лимит VK
        transaction.paymentSystem == PaymentSystem.VKPAY &&
                transaction.amount > MAX_VK_TRANSACTION_LIMIT -> {
            limitExceedMessagePrint(LimitExceedType.TRANSACTION, MAX_VK_TRANSACTION_LIMIT)
            true
        }

        // Проверяем месячный лимит VK
        transaction.paymentSystem == PaymentSystem.VKPAY &&
                (totalDay > MAX_VK_MONTHLY_LIMIT || totalMonth > MAX_VK_MONTHLY_LIMIT) -> {
            limitExceedMessagePrint(LimitExceedType.MONTHLY, MAX_VK_MONTHLY_LIMIT)
            true
        }

        // Проверяем дневной лимит для карт
        totalDay > MAX_DAILY_LIMIT -> {
            limitExceedMessagePrint(LimitExceedType.DAILY, MAX_DAILY_LIMIT)
            true
        }
        // Проверяем месячный лимит для карт
        totalMonth > MAX_MONTHLY_LIMIT -> {
            limitExceedMessagePrint(LimitExceedType.MONTHLY, MAX_MONTHLY_LIMIT)
            true
        }

        else -> false
    }
}

// Формируем и выводим сообщение о превышении лимита
fun limitExceedMessagePrint(exceedType: LimitExceedType, limit: Double) {
    val message = when (exceedType) {
        LimitExceedType.TRANSACTION -> "Операция отменена. " +
                "Нельзя перевести более $limit руб. за один раз. " +
                "Попробуйте уменьшить сумму или сделать несколько переводов."

        LimitExceedType.DAILY -> "Операция отменена. " +
                "Нельзя перевести более $limit руб. в сутки. " +
                "Попробуйте уменьшить сумму или выполнить операцию в другой день."

        LimitExceedType.MONTHLY -> "Операция отменена. " +
                "Нельзя перевести более $limit руб. в месяц. " +
                "Попробуйте  уменьшить сумму или выполнить операцию в следующем месяце."
    }
    println(message)
}