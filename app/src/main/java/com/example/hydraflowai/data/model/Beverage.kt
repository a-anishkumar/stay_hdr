package com.example.hydraflowai.data.model

enum class Beverage(val displayName: String, val hydrationFactor: Float, val colorHex: String) {
    WATER("Water", 1.0f, "#2196F3"),
    COCONUT_WATER("Coconut Water", 0.95f, "#8BC34A"),
    FRESH_JUICE("Fresh Juice", 0.85f, "#FF9800"),
    MILK("Milk", 0.85f, "#FFFFFF"),
    SPORTS_DRINKS("Sports Drink", 0.80f, "#00BCD4"),
    TEA("Tea", 0.70f, "#4CAF50"),
    COFFEE("Coffee", 0.60f, "#795548"),
    CARBONATED_DRINKS("Soda/Carbonated", 0.40f, "#9C27B0")
}
