package com.rmap.mobile.features.airoadmap.presentation.components

import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiGeneratedRoadmapUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapFormError
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionOptionUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapStep
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState

internal object AiRoadmapPreviewData {
    val generatedRoadmaps = listOf(
        AiGeneratedRoadmapUiModel(
            id = "android-dev",
            title = "Android Developer",
            lessonsCount = 24,
            durationWeeks = 8,
            createdDaysAgo = 0
        ),
        AiGeneratedRoadmapUiModel(
            id = "frontend-dev",
            title = "Frontend Developer",
            lessonsCount = 32,
            durationWeeks = 12,
            createdDaysAgo = 3
        ),
        AiGeneratedRoadmapUiModel(
            id = "backend-api",
            title = "Backend API Engineer",
            lessonsCount = 28,
            durationWeeks = 10,
            createdDaysAgo = 12
        )
    )

    val optionQuestion = AiRoadmapQuestionUiModel(
        id = "current-level",
        skillName = "Android Developer",
        prompt = "How would you describe your current Android level?",
        options = listOf(
            AiRoadmapQuestionOptionUiModel("new", "1", "I am completely new"),
            AiRoadmapQuestionOptionUiModel("basics", "2", "I know the basics"),
            AiRoadmapQuestionOptionUiModel("projects", "3", "I can build small projects"),
            AiRoadmapQuestionOptionUiModel("production", "4", "I have production experience")
        ),
        selectedOptionId = "projects"
    )

    val customQuestion = AiRoadmapQuestionUiModel(
        id = "constraints",
        skillName = "Study plan",
        prompt = "Any constraints RMap should consider before creating your plan?",
        options = emptyList(),
        isCustomAnswerSelected = true,
        customAnswer = "I can study more on weekends and prefer hands-on projects."
    )

    val generationStatus = AiRoadmapGenerationStatus(
        phase = AiRoadmapGenerationPhase.Running,
        progressPercent = 64,
        stageLabel = "Building weekly milestones"
    )

    val libraryState = AiRoadmapUiState(
        step = AiRoadmapStep.Library,
        generatedRoadmaps = generatedRoadmaps
    )

    val setupState = AiRoadmapUiState(
        step = AiRoadmapStep.Setup,
        topic = "Android Developer",
        deadlineEpochMillis = System.currentTimeMillis() + 45L * 24L * 60L * 60L * 1000L,
        dailyStudyHours = 3.5f
    )

    val setupLoadingState = setupState.copy(isLoadingQuestions = true)

    val setupErrorState = setupState.copy(formError = AiRoadmapFormError.DeadlineRequired)

    val questionsState = AiRoadmapUiState(
        step = AiRoadmapStep.Questions,
        topic = "Android Developer",
        questions = listOf(optionQuestion, customQuestion),
        currentQuestionIndex = 0
    )

    val questionsErrorState = questionsState.copy(
        currentQuestionIndex = 1,
        formError = AiRoadmapFormError.AnswerAllQuestions
    )

    val generatingState = AiRoadmapUiState(
        step = AiRoadmapStep.Generating,
        topic = "Android Developer",
        roleCategory = "Mobile development",
        deadlineEpochMillis = System.currentTimeMillis() + 84L * 24L * 60L * 60L * 1000L,
        dailyStudyHours = 2.5f,
        questions = listOf(
            optionQuestion,
            customQuestion,
            optionQuestion.copy(id = "q3", skillName = "UI/UX", prompt = "Do you have experience with Compose?"),
            optionQuestion.copy(id = "q4", skillName = "Architecture", prompt = "What is your favorite architecture?"),
            optionQuestion.copy(id = "q5", skillName = "Infrastructure", prompt = "How do you handle dependency injection?")
        ),
        generationStatus = generationStatus
    )
}
