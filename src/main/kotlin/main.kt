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

// Превышен дневной лимит
val request1 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 22_000.0,
    amountPerMonth = 300_000.0,
    amount = 140_000.0
)

// Превышен месячный лимит
val request2 = Request(
    paymentSystem = PaymentSystem.VISA,
    amountPerDay = 130_000.0,
    amountPerMonth = 590_000.0,
    amount = 11_000.0
)

// Мастеркард с комиссией
val request3 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 0_000.0,
    amountPerMonth = 20_000.0,
    amount = 150_000.0
)

// Мастеркард без комиссии
val request4 = Request(
    paymentSystem = PaymentSystem.MASTERCARD,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 30_000.0
)

// Виза с комиссией 35 р.
val request5 = Request(
    paymentSystem = PaymentSystem.VISA,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 2_000.0
)

// Виза с комиссией > 35 р.
val request6 = Request(
    paymentSystem = PaymentSystem.VISA,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 15_000.0
)

// МИР без комиссии.
val request7 = Request(
    paymentSystem = PaymentSystem.MIR,
    amountPerDay = 10_000.0,
    amountPerMonth = 20_000.0,
    amount = 140_000.0
)

fun main() {

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
}


// вычисление комиссии
fun calculateCommission(
    request: Request,
): Double {
    // Если один из лимитов превышен, то перевод не произойдёт, следовательно комиссии не будет.
    // Во время проверки функция isLimitsExeeded выведет соответствующие сообщения в консоль.
    if (isLimitsExeeded(request)) {
        return 0.0
    }

    return when (request.paymentSystem) {
        PaymentSystem.VISA -> calculateVisaCommission(request.amount)
        PaymentSystem.MASTERCARD -> calculateMastercardCommission(request.amount)
        PaymentSystem.MIR -> 0.0
        else -> {
            println("Неизвестный тип карты.")
            0.0
        }
    }
}

// Функция вычисляет комиссию для карты Visa
fun calculateVisaCommission(amountTransfer: Double): Double {
    val commission = amountTransfer * VISA_COMMISSION_RATE
    return if (amountTransfer * VISA_COMMISSION_RATE > MIN_VISA_COMMISSION)
            amountTransfer * VISA_COMMISSION_RATE
            else MIN_VISA_COMMISSION
}

// Функция вычисляет комиссию для Mastercard
fun calculateMastercardCommission(amountTransfer: Double): Double {
    return if (amountTransfer <= MASTERCARD_FREE_TRANSACTIONS_LIMIT)
        0.0
    else
        (amountTransfer - MASTERCARD_FREE_TRANSACTIONS_LIMIT) * MASTERCARD_COMMISSION_RATE + MASTERCARD_FIXED_FEE
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
