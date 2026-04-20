---
name: api-client
description: >
  Generates the API layer for this project from openapi.yaml.
  Use when creating or updating: Retrofit service interfaces, DTO classes, or Mapper functions.
  Source of truth: openapi.yaml at project root — NEVER invent fields or endpoints.
---

## Layer Structure

```
data/
  remote/
    api/         ← Retrofit @Service interfaces
    model/       ← DTO data classes (request & response)
  mapper/        ← DTO → Domain model mapping functions
```

## Rules

- **ALWAYS** read `openapi.yaml` before writing any DTO or service method.
- **NEVER** invent fields, endpoints, or status codes not defined in the spec.
- DTOs **MUST** use `@SerializedName` (Gson) to match exact JSON field names.
- DTOs and Entities **NEVER** leak outside the `data` layer.
- All mapping happens **only** in `data/mapper/` — never in ViewModel or UseCase.
- Error responses must be mapped to domain `Result` types in the repository, not thrown raw.

## Code Templates

### Retrofit Service Interface

```kotlin
interface RoadmapApiService {
    @GET("roadmaps/{id}")
    suspend fun getRoadmap(
        @Path("id") id: String
    ): Response<RoadmapResponseDto>

    @POST("roadmaps")
    suspend fun createRoadmap(
        @Body request: CreateRoadmapRequestDto
    ): Response<RoadmapResponseDto>
}
```

### DTO Classes

```kotlin
// Response DTO — matches openapi.yaml schema exactly
data class RoadmapResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("steps") val steps: List<RoadmapStepDto>
)

// Nested DTO
data class RoadmapStepDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("completed") val completed: Boolean
)
```

### Mapper (DTO → Domain)

```kotlin
// Extension function — lives in data/mapper/RoadmapMapper.kt
fun RoadmapResponseDto.toDomain(): Roadmap = Roadmap(
    id = id,
    title = title,
    steps = steps.map { it.toDomain() }
)

fun RoadmapStepDto.toDomain(): RoadmapStep = RoadmapStep(
    id = id,
    name = name,
    isCompleted = completed
)
```

### Repository Implementation (error mapping)

```kotlin
override suspend fun getRoadmap(id: String): Result<Roadmap> = runCatching {
    val response = apiService.getRoadmap(id)
    if (response.isSuccessful) {
        response.body()?.toDomain() ?: error("Empty response body")
    } else {
        error("API error ${response.code()}: ${response.errorBody()?.string()}")
    }
}
```

## Checklist

- [ ] DTO field names match `openapi.yaml` exactly (use `@SerializedName`)
- [ ] No DTO type referenced outside `data/` package
- [ ] Mapper file placed in `data/mapper/`
- [ ] Repository wraps calls in `runCatching` or explicit `Result`
- [ ] All errors logged with Timber, not leaked raw to ViewModel
