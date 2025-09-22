package ru.netology

import kotlin.Double

const val MAX_DAILY_LIMIT = 150_000.0
const val MAX_MONTHLY_LIMIT = 600_000.0

const val MASTERCARD_FREE_TRANSACTIONS_LIMIT = 75_000.0
const val MASTERCARD_COMMISSION_RATE = 0.006
const val MASTERCARD_FIXED_FEE = 20.0

const val VISA_COMMISSION_RATE = 0.0075
const val MIN_VISA_COMMISSION = 35.0

// Перечисление для платёжных систем
enum class PaymentSystem {
    MASTERCARD,
    MIR,
    VISA
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
// запрос по умолчанию
val request_default = Request(
    paymentSystem = PaymentSystem.MIR,
    amountPerDay = 0.0,
    amountPerMonth = 0.0,
    amount = 1.0
    // ожидаем 0.0
)

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
    // ожидаем 147.5
)

// МИР без комиссии.
val request10 = Request(
    paymentSystem = PaymentSystem.MIR,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 140_000.0
    // ожидаем 0.0
)

fun main() {

    // проверяем работу calculateCommission() с параметром по умолчанию
    println("Комиссия для MIR " +
            "при переводе в 1 руб. " +
            "составит " + calculateCommission()+" руб.")

    calculateCommission(request1)
    calculateCommission(request2)

    println("Комиссия для ${request3.paymentSystem} " +
            "при переводе в ${request3.amount} руб. " +
            "составит " + calculateCommission(request3)+" руб.")

    println("Комиссия для ${request4.paymentSystem} " +
            "при переводе в ${request4.amount} руб. " +
            "составит " + calculateCommission(request4)+" руб.")

    println("Комиссия для ${request5.paymentSystem} " +
            "при переводе в ${request5.amount} руб. " +
            "составит " + calculateCommission(request5)+" руб.")

    println("Комиссия для ${request6.paymentSystem} " +
            "при переводе в ${request6.amount} руб. " +
            "составит " + calculateCommission(request6)+" руб.")

    println("Комиссия для ${request7.paymentSystem} " +
            "при переводе в ${request7.amount} руб. " +
            "составит " + calculateCommission(request7)+" руб.")

    println("Комиссия для ${request8.paymentSystem} " +
            "при переводе в ${request8.amount} руб. " +
            "составит " + calculateCommission(request8)+" руб.")

    println("Комиссия для ${request9.paymentSystem} " +
            "при переводе в ${request9.amount} руб. " +
            "составит " + calculateCommission(request9)+" руб.")

    println("Комиссия для ${request10.paymentSystem} " +
            "при переводе в ${request10.amount} руб. " +
            "составит " + calculateCommission(request10)+" руб.")
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

    return when (request.paymentSystem) {
        PaymentSystem.VISA -> calculateVisaCommission(request.amount)
        PaymentSystem.MASTERCARD -> calculateMastercardCommission(request)
        PaymentSystem.MIR -> 0.0
        else -> {
            println("Неизвестный тип карты.")
            0.0
        }
    }
}

// Функция вычисляет комиссию для карты Visa
fun calculateVisaCommission(amount: Double): Double {
    val commission = amount * VISA_COMMISSION_RATE
    return if (amount * VISA_COMMISSION_RATE > MIN_VISA_COMMISSION)
        amount * VISA_COMMISSION_RATE
            else MIN_VISA_COMMISSION
}

// Функция вычисляет комиссию для Mastercard
fun calculateMastercardCommission(request: Request): Double {
    val totalDay = request.amountPerDay + request.amount
    val totalMonth = request.amountPerMonth + request.amount

    return when {
        // 1. Предыдущие платежи уже превысили лимит
        request.amountPerDay >= MASTERCARD_FREE_TRANSACTIONS_LIMIT || request.amountPerMonth >= MASTERCARD_FREE_TRANSACTIONS_LIMIT ->
            request.amount * MASTERCARD_COMMISSION_RATE + MASTERCARD_FIXED_FEE

        // 2. Общая сумма за день превышает лимит
        totalDay > MASTERCARD_FREE_TRANSACTIONS_LIMIT ->
            (totalDay - MASTERCARD_FREE_TRANSACTIONS_LIMIT) * MASTERCARD_COMMISSION_RATE + MASTERCARD_FIXED_FEE

        // 3. Общая сумма за месяц превышает лимит
        totalMonth > MASTERCARD_FREE_TRANSACTIONS_LIMIT ->
            (totalMonth - MASTERCARD_FREE_TRANSACTIONS_LIMIT) * MASTERCARD_COMMISSION_RATE + MASTERCARD_FIXED_FEE

        // 4. Текущий платёж превышает лимит
        request.amount > MASTERCARD_FREE_TRANSACTIONS_LIMIT ->
            (request.amount - MASTERCARD_FREE_TRANSACTIONS_LIMIT) * MASTERCARD_COMMISSION_RATE + MASTERCARD_FIXED_FEE

        // Остальные случаи — комиссия не взимается
        else -> 0.0
    }
}

// Функция проверяет дневной и месячный лимиты и выводит соответствующие сообщения.
// Если какой-то из лимитов превышен - вернёт true, если перевод укладывается в лимиты - false
fun isLimitsExeeded(
    request: Request
): Boolean {
    //Проверяем, укладывается ли перевод в дневной лимит.
    val dailyLimitExceeded = request.amountPerDay + request.amount > MAX_DAILY_LIMIT

    //Проверяем, укладывается ли перевод в месячный лимит.
    val monthlyLimitExceeded = request.amountPerMonth + request.amount > MAX_MONTHLY_LIMIT

    // Формируем сообщение об ошибке
    val errorMessage = when {
        dailyLimitExceeded -> "Операция отменена. " +
                "Нельзя перевести более $MAX_DAILY_LIMIT рублей в сутки. " +
                "Попробуйте уменьшить сумму или выполнить операцию в другой день."

        monthlyLimitExceeded -> "Операция отменена. " +
                "Нельзя перевести более $MAX_MONTHLY_LIMIT" +
                "рублей в месяц. " +
                "Попробуйте  уменьшить сумму или выполнить операцию в следующем месяце."
        else -> null
    }

    // Если есть сообщение об ошибке, выводим его
    errorMessage?.let { println(it) }
    return dailyLimitExceeded || monthlyLimitExceeded
}
