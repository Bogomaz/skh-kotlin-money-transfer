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
    VKPAY
}

// Типы превышения лимитов
enum class LimitExeedType {
    TRANSACTION,
    DAILY,
    MONTHLY
}


// Дата-класс, в котором будут храниться все параметры,
// необходимые для функции, вычисляющей попадание в лимиты
data class Request(
    val paymentSystem: PaymentSystem,
    val amountPerDay: Double,
    val amountPerMonth: Double,
    val amount: Double
)

// Создаём несколько переменных для проверки

// Превышен дневной лимит
val request1 = Request(
    paymentSystem = PaymentSystem.MIR,
    amountPerDay = 22_000.0,
    amountPerMonth = 300_000.0,
    amount = 140_000.0
    // ожидаем отмену операции и 0.0
)

// Превышен месячный лимит
val request2 = Request(
    paymentSystem = PaymentSystem.VISA,
    amountPerDay = 130_000.0,
    amountPerMonth = 590_000.0,
    amount = 11_000.0
    // ожидаем отмену операции и 0.0
)

// Мастеркард. Текущий платёж превышает лимит
val request3 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 0.0,
    amountPerMonth = 0.0,
    amount = 140_000.0
    // ожидаем 410.0
)

// Мастеркард. Предыдущие платежи уже превысили лимит
val request4 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 60_000.0,
    amountPerMonth = 85_000.0,
    amount = 50_000.0
    // ожидаем 320.0
)

// Мастеркард. Текущий плотёж + сумма за день превышают лимит
val request5 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 20_000.0,
    amountPerMonth = 40_000.0,
    amount = 60_000.0
    // ожидаем 50.0
)

// Мастеркард. Текущий плотёж + сумма за месяц превышает лимит
val request6 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 30_000.0,
    amountPerMonth = 70_000.0,
    amount = 20_000.0
    // ожидаем 110.0
)

// Мастеркард без комиссии
val request7 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 30_000.0
    // ожидаем 0.0
)

// Виза с комиссией 35 р.
val request8 = Request(
    paymentSystem = PaymentSystem.VISA,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 2_000.0
    // ожидаем 35.0
)

// Виза с комиссией > 35 р.
val request9 = Request(
    paymentSystem = PaymentSystem.VISA,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 15_000.0
    // ожидаем 112.5
)

// МИР без комиссии.
val request10 = Request(
    paymentSystem = PaymentSystem.MIR,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 140_000.0
    // ожидаем 1050.0
)

fun main() {

    // проверяем работу calculateCommission() с параметром по умолчанию
    println(
        "Комиссия для MIR " +
                "при переводе в 1 руб. " +
                "составит " + calculateCommission() + " руб."
    )

    calculateCommission(request1)
    calculateCommission(request2)

    println(
        "Комиссия для ${request3.paymentSystem} " +
                "при переводе в ${request3.amount} руб. " +
                "составит " + calculateCommission(request3) + " руб."
    )

    println(
        "Комиссия для ${request4.paymentSystem} " +
                "при переводе в ${request4.amount} руб. " +
                "составит " + calculateCommission(request4) + " руб."
    )

    println(
        "Комиссия для ${request5.paymentSystem} " +
                "при переводе в ${request5.amount} руб. " +
                "составит " + calculateCommission(request5) + " руб."
    )

    println(
        "Комиссия для ${request6.paymentSystem} " +
                "при переводе в ${request6.amount} руб. " +
                "составит " + calculateCommission(request6) + " руб."
    )

    println(
        "Комиссия для ${request7.paymentSystem} " +
                "при переводе в ${request7.amount} руб. " +
                "составит " + calculateCommission(request7) + " руб."
    )

    println(
        "Комиссия для ${request8.paymentSystem} " +
                "при переводе в ${request8.amount} руб. " +
                "составит " + calculateCommission(request8) + " руб."
    )

    println(
        "Комиссия для ${request9.paymentSystem} " +
                "при переводе в ${request9.amount} руб. " +
                "составит " + calculateCommission(request9) + " руб."
    )

    println(
        "Комиссия для ${request10.paymentSystem} " +
                "при переводе в ${request10.amount} руб. " +
                "составит " + calculateCommission(request10) + " руб."
    )
}


// вычисление комиссии
fun calculateCommission(
    request: Request = Request(
        paymentSystem = PaymentSystem.MIR,
        amountPerDay = 0.0,
        amountPerMonth = 0.0,
        amount = 1.0
    )
): Double {
    // Если один из лимитов превышен, то перевод не произойдёт, следовательно комиссии не будет.
    // Во время проверки функция isLimitsExeeded выведет соответствующие сообщения в консоль.
    if (isLimitsExeeded(request)) {
        return 0.0
    }

    return when {
        request.paymentSystem == PaymentSystem.VISA ||
                request.paymentSystem == PaymentSystem.MIR ->
                    calculateVisaMirCommission(request.amount)
        request.paymentSystem == PaymentSystem.MASTERCARD ||
                request.paymentSystem == PaymentSystem.MAESTRO ->
                    calculateMaestroMastercardCommission(request)
        request.paymentSystem == PaymentSystem.VKPAY -> 0.0
        else -> {
            println("Неизвестный тип карты.")
            0.0
        }
    }
}

// Функция вычисляет комиссию для карт Visa или Мир
fun calculateVisaMirCommission(amount: Double): Double {
    val commission = amount * VISA_MIR_COMMISSION_RATE
    return if (amount * VISA_MIR_COMMISSION_RATE > MIN_VISA_MIR_COMMISSION)
        amount * VISA_MIR_COMMISSION_RATE
    else MIN_VISA_MIR_COMMISSION
}

// Функция вычисляет комиссию для Mastercard или Maestro
fun calculateMaestroMastercardCommission(request: Request): Double {
    val totalDay = request.amountPerDay + request.amount
    val totalMonth = request.amountPerMonth + request.amount

    return when {
        // 0. Платёж меньше установленной суммы
        request.amount < MIN_MAESTRO_MASTERCARD_FREE_LIMIT ->
            request.amount * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE
        // 1. Предыдущие платежи уже превысили лимит
        request.amountPerDay >= MAX_MAESTRO_MASTERCARD_FREE_LIMIT || request.amountPerMonth >= MAX_MAESTRO_MASTERCARD_FREE_LIMIT ->
            request.amount * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE

        // 2. Текущий платёж + сумма за день превышает лимит
        totalDay > MAX_MAESTRO_MASTERCARD_FREE_LIMIT ->
            (totalDay - MAX_MAESTRO_MASTERCARD_FREE_LIMIT) * MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE

        // 3. Текущий платёж + сумма за месяц превышает лимит
        totalMonth > MAX_MAESTRO_MASTERCARD_FREE_LIMIT ->
            (totalMonth - MAX_MAESTRO_MASTERCARD_FREE_LIMIT) *MAESTRO_MASTERCARD_COMMISSION_RATE + MAESTRO_MASTERCARD_FIXED_FEE

        // Остальные случаи — комиссия не взимается
        else -> 0.0
    }
}

// Функция проверяет дневной и месячный лимиты и выводит соответствующие сообщения.
// Если какой-то из лимитов превышен - вернёт true, если перевод укладывается в лимиты - false
fun isLimitsExeeded(request: Request): Boolean {
    val totalDay = request.amountPerDay + request.amount
    val totalMonth = request.amountPerMonth + request.amount
    return when {
        // Проверяем дневной лимит VK
        request.paymentSystem == PaymentSystem.VKPAY &&
                request.amount > MAX_VK_TRANSACTION_LIMIT -> {
            limitExeedMessagePrint(LimitExeedType.TRANSACTION, MAX_VK_TRANSACTION_LIMIT)
            true
        }

        // Проверяем месячный лимит VK
        request.paymentSystem == PaymentSystem.VKPAY &&
                (totalDay > MAX_VK_MONTHLY_LIMIT || totalMonth > MAX_VK_MONTHLY_LIMIT) -> {
            limitExeedMessagePrint(LimitExeedType.MONTHLY, MAX_VK_MONTHLY_LIMIT)
            true
        }

        // Проверяем дневной лимит для карт
        totalDay > MAX_DAILY_LIMIT -> {
            limitExeedMessagePrint(LimitExeedType.DAILY, MAX_DAILY_LIMIT)
            true
        }
        // Проверяем месячный лимит для карт
        totalMonth > MAX_MONTHLY_LIMIT -> {
            limitExeedMessagePrint(LimitExeedType.MONTHLY, MAX_MONTHLY_LIMIT)
            true
        }
        else -> false
    }
}

// Формируем и выводим сообщение о превышении лимита
fun limitExeedMessagePrint(exeedType: LimitExeedType, limit: Double) {
    val message = when (exeedType) {
        LimitExeedType.TRANSACTION -> "Операция отменена. " +
                "Нельзя перевести более $limit руб. за один раз. " +
                "Попробуйте уменьшить сумму или сделать несколько переводов."

        LimitExeedType.DAILY -> "Операция отменена. " +
                "Нельзя перевести более $limit руб. в сутки. " +
                "Попробуйте уменьшить сумму или выполнить операцию в другой день."

        LimitExeedType.MONTHLY -> "Операция отменена. " +
                "Нельзя перевести более $limit руб. в месяц. " +
                "Попробуйте  уменьшить сумму или выполнить операцию в следующем месяце."
    }
    message?.let {
        println(it)
    }
}