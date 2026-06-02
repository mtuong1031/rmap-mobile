package com.rmap.mobile.features.roadmap.domain.model

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

fun String.toRoadmapCategoryDisplayLabel(fallbackLabel: String? = null): String {
    return fallbackLabel?.takeIf { it.isNotBlank() } ?: toReadableCategoryLabel()
}

fun String.toHomeBrowseCategoryLabel(fallbackLabel: String? = null): String {
    return when (toRoadmapCategoryKey()) {
        CATEGORY_WEB_DEVELOPMENT -> "Web"
        CATEGORY_FRAMEWORKS -> "Frameworks"
        CATEGORY_ABSOLUTE_BEGINNERS -> "Beginner"
        CATEGORY_LANGUAGES_AND_PLATFORMS -> "Languages"
        CATEGORY_DEVOPS -> "DevOps"
        CATEGORY_DATABASES -> "Databases"
        CATEGORY_COMPUTER_SCIENCE -> "CS"
        CATEGORY_DESIGN -> "Design"
        CATEGORY_BEST_PRACTICES -> "Best Practices"
        CATEGORY_AI_AND_MACHINE_LEARNING -> "AI"
        CATEGORY_DATA_ANALYSIS -> "Data"
        CATEGORY_MOBILE_DEVELOPMENT -> "Mobile"
        CATEGORY_MANAGEMENT -> "Management"
        CATEGORY_GAME_DEVELOPMENT -> "Game"
        CATEGORY_BLOCKCHAIN -> "Blockchain"
        CATEGORY_CYBER_SECURITY -> "Security"
        else -> fallbackLabel?.takeIf { it.isNotBlank() } ?: toReadableCategoryLabel()
    }
}

fun String.toRoadmapCategoryIcon(): LearningTopicIcon {
    return when (toRoadmapCategoryKey()) {
        CATEGORY_WEB_DEVELOPMENT,
        CATEGORY_FRAMEWORKS,
        CATEGORY_ABSOLUTE_BEGINNERS,
        CATEGORY_BEST_PRACTICES,
        CATEGORY_BLOCKCHAIN -> LearningTopicIcon.Code
        CATEGORY_LANGUAGES_AND_PLATFORMS,
        CATEGORY_DEVOPS -> LearningTopicIcon.Terminal
        CATEGORY_DATABASES -> LearningTopicIcon.Storage
        CATEGORY_DATA_ANALYSIS,
        CATEGORY_MANAGEMENT -> LearningTopicIcon.DataObject
        CATEGORY_COMPUTER_SCIENCE -> LearningTopicIcon.Science
        CATEGORY_DESIGN -> LearningTopicIcon.Palette
        CATEGORY_AI_AND_MACHINE_LEARNING -> LearningTopicIcon.SmartToy
        CATEGORY_MOBILE_DEVELOPMENT -> LearningTopicIcon.Devices
        CATEGORY_GAME_DEVELOPMENT -> LearningTopicIcon.Game
        CATEGORY_CYBER_SECURITY -> LearningTopicIcon.Security
        else -> LearningTopicIcon.Code
    }
}

private fun String.toRoadmapCategoryKey(): String {
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

private fun String.toReadableCategoryLabel(): String {
    return trim()
        .lowercase()
        .split(Regex("[_\\s-]+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
}
