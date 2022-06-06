package dev.five_star.pingpong

data class Prefix(val country: String, val prefix: String)

val preFixes = listOf(
    Prefix("Germany", "+49"),
    Prefix("Austria", "+43"),
    Prefix("Swiss", "+41"),
    Prefix("Italy", "+39"),
)

