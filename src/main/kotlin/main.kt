package ru.netology

const val MIN_FIX_COMMISSION: Double = 35.0
const val COMMISSION_PERCENT: Double = 0.75

fun main() {
    val amount: Double = 5_000.0
    val commission: Double = if(amount * 0.0075 < MIN_FIX_COMMISSION) MIN_FIX_COMMISSION else amount * 0.0075
    println("Для перевода в $amount руб. комиссия составит $commission руб.")
}

