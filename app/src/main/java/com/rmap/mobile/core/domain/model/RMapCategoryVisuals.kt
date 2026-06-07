package com.rmap.mobile.core.domain.model

enum class RMapCategoryIconKey {
    Code,
    DataObject,
    Devices,
    Game,
    Palette,
    Science,
    Security,
    SmartToy,
    Storage,
    Terminal
}

private const val CATEGORY_WEB_DEVELOPMENT = "WEB_DEVELOPMENT"
private const val CATEGORY_FRAMEWORKS = "FRAMEWORKS"
private const val CATEGORY_ABSOLUTE_BEGINNERS = "ABSOLUTE_BEGINNERS"
private const val CATEGORY_LANGUAGES_AND_PLATFORMS = "LANGUAGES_AND_PLATFORMS"
private const val CATEGORY_DEVOPS = "DEVOPS"
private const val CATEGORY_DATABASES = "DATABASES"
private const val CATEGORY_COMPUTER_SCIENCE = "COMPUTER_SCIENCE"
private const val CATEGORY_DESIGN = "DESIGN"
private const val CATEGORY_BEST_PRACTICES = "BEST_PRACTICES"
private const val CATEGORY_AI_AND_MACHINE_LEARNING = "AI_AND_MACHINE_LEARNING"
private const val CATEGORY_DATA_ANALYSIS = "DATA_ANALYSIS"
private const val CATEGORY_MOBILE_DEVELOPMENT = "MOBILE_DEVELOPMENT"
private const val CATEGORY_MANAGEMENT = "MANAGEMENT"
private const val CATEGORY_GAME_DEVELOPMENT = "GAME_DEVELOPMENT"
private const val CATEGORY_BLOCKCHAIN = "BLOCKCHAIN"
private const val CATEGORY_CYBER_SECURITY = "CYBER_SECURITY"

fun String.toRMapCategoryIconKey(): RMapCategoryIconKey {
    return when (toRMapCategoryKey()) {
        CATEGORY_WEB_DEVELOPMENT,
        CATEGORY_FRAMEWORKS,
        CATEGORY_ABSOLUTE_BEGINNERS,
        CATEGORY_BEST_PRACTICES,
        CATEGORY_BLOCKCHAIN -> RMapCategoryIconKey.Code
        CATEGORY_LANGUAGES_AND_PLATFORMS,
        CATEGORY_DEVOPS -> RMapCategoryIconKey.Terminal
        CATEGORY_DATABASES -> RMapCategoryIconKey.Storage
        CATEGORY_DATA_ANALYSIS,
        CATEGORY_MANAGEMENT -> RMapCategoryIconKey.DataObject
        CATEGORY_COMPUTER_SCIENCE -> RMapCategoryIconKey.Science
        CATEGORY_DESIGN -> RMapCategoryIconKey.Palette
        CATEGORY_AI_AND_MACHINE_LEARNING -> RMapCategoryIconKey.SmartToy
        CATEGORY_MOBILE_DEVELOPMENT -> RMapCategoryIconKey.Devices
        CATEGORY_GAME_DEVELOPMENT -> RMapCategoryIconKey.Game
        CATEGORY_CYBER_SECURITY -> RMapCategoryIconKey.Security
        else -> RMapCategoryIconKey.Code
    }
}

fun String.toRMapCategoryKey(): String {
    return trim()
        .uppercase()
        .replace(Regex("[^A-Z0-9]+"), "_")
        .trim('_')
        .let { key ->
            when (key) {
                "WEB_DEVELOPEMENT" -> CATEGORY_WEB_DEVELOPMENT
                "ABSOLUTE_BEGINNER" -> CATEGORY_ABSOLUTE_BEGINNERS
                "LANGUAGES_PLATFORMS" -> CATEGORY_LANGUAGES_AND_PLATFORMS
                "AI_MACHINE_LEARNING" -> CATEGORY_AI_AND_MACHINE_LEARNING
                "CYEBER_SECURITY" -> CATEGORY_CYBER_SECURITY
                else -> key
            }
        }
}
