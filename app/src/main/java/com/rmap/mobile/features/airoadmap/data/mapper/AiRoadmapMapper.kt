package com.rmap.mobile.features.airoadmap.data.mapper

import com.rmap.mobile.features.airoadmap.data.model.AssessmentAnswerDto
import com.rmap.mobile.features.airoadmap.data.model.GenerateRoadmapRequestDto
import com.rmap.mobile.features.airoadmap.data.model.OnboardingQuizResponseDto
import com.rmap.mobile.features.airoadmap.data.model.RoadmapResponseDto
import com.rmap.mobile.features.airoadmap.domain.model.AiGeneratedRoadmap
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestionOption
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuizResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun OnboardingQuizResponseDto.toDomain(goal: String): AiRoadmapQuizResult {
    return AiRoadmapQuizResult(
        roleCategory = roleCategory.toBackendRoleCategory(),
        questions = questions.mapIndexed { questionIndex, question ->
            AiRoadmapQuestion(
                id = "question-${questionIndex + 1}",
                skillName = goal,
                prompt = question.question,
                options = question.possibleAnswers.mapIndexed { optionIndex, answer ->
                    AiRoadmapQuestionOption(
                        id = "question-${questionIndex + 1}-option-${optionIndex + 1}",
                        label = answer
                    )
                }
            )
        }
    )
}

fun AiRoadmapGenerationRequest.toDto(): GenerateRoadmapRequestDto {
    val roleCategory = draft.roleCategory.orEmpty().toBackendRoleCategory()
    return GenerateRoadmapRequestDto(
        goal = draft.topic,
        roleCategory = roleCategory,
        hoursPerDay = draft.dailyStudyHours.toDouble(),
        deadlineDate = draft.deadlineEpochMillis.toDateOnlyString(),
        quizAnswers = answers.map { it.toDto() }
    )
}

fun RoadmapResponseDto.toDomain(lessonsCount: Int): AiGeneratedRoadmap {
    return AiGeneratedRoadmap(
        id = id,
        title = title,
        lessonsCount = lessonsCount.coerceAtLeast(0),
        durationWeeks = (estimatedWeeks ?: DEFAULT_DURATION_WEEKS).coerceAtLeast(1),
        generatedAtEpochMillis = generatedAt.parseIsoTimestamp()
    )
}

fun String.toBackendRoleCategory(): String {
    return trim()
        .replace("-", "_")
        .uppercase(Locale.US)
}

private fun AiRoadmapAnswer.toDto(): AssessmentAnswerDto {
    return AssessmentAnswerDto(
        question = question,
        answer = answer
    )
}

private fun Long.toDateOnlyString(): String {
    val formatter = SimpleDateFormat(DATE_ONLY_PATTERN, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(this))
}

private fun String.parseIsoTimestamp(): Long? {
    ISO_PATTERNS.forEach { pattern ->
        val formatter = SimpleDateFormat(pattern, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val parsed = runCatching { formatter.parse(this) }.getOrNull()
        if (parsed != null) return parsed.time
    }

    return null
}

private const val DATE_ONLY_PATTERN = "yyyy-MM-dd"
private const val DEFAULT_DURATION_WEEKS = 1

private val ISO_PATTERNS = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss'Z'"
)
