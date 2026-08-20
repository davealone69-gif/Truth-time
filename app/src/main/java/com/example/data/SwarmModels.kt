package com.example.data

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
)

data class LlmModel(
    val id: String,
    val name: String,
    val provider: String,
    val isFree: Boolean = true,
    val description: String = "",
)

object FreeModels {
    val list = listOf(
        LlmModel("llama3-8b-8192", "Llama 3 8B", "Groq", true, "Fast free tier"),
        LlmModel("llama3-70b-8192", "Llama 3 70B", "Groq", true, "Strong reasoning"),
        LlmModel("gemma2-9b-it", "Gemma 2 9B", "Groq", true, "Google open model"),
        LlmModel("mixtral-8x7b-32768", "Mixtral 8x7B", "Groq", true, "Mixture of Experts"),
        LlmModel("openrouter/free", "OpenRouter Free", "OpenRouter", true, "Rotating free models"),
        LlmModel("huggingface/zephyr", "Zephyr 7B", "Hugging Face", true, "Instruction tuned"),
        LlmModel("local/placeholder", "Local (soon)", "On-device", true, "llama.cpp / MLC coming"),
    )
}

enum class SwarmTopology {
    SEQUENTIAL,
    PARALLEL,
    DEBATE,
    HIERARCHICAL,
    BUILDER_CRITIC,
}

data class SwarmAgent(
    val id: String = System.currentTimeMillis().toString() + (0..999).random(),
    val name: String,
    val role: String,
    val modelId: String,
    val systemPrompt: String,
)

data class SwarmMessage(
    val id: String = System.currentTimeMillis().toString() + (0..999).random(),
    val agentName: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
)

data class SwarmConfig(
    val id: String = System.currentTimeMillis().toString(),
    val name: String,
    val topology: SwarmTopology,
    val agents: List<SwarmAgent>,
    val task: String = "",
)

object SwarmDefaults {
    val defaultAgents = listOf(
        SwarmAgent(
            name = "Planner",
            role = "Planner",
            modelId = "llama3-8b-8192",
            systemPrompt = "You are a senior software planner. Break the building task into clear, ordered steps. Output only the plan.",
        ),
        SwarmAgent(
            name = "Coder",
            role = "Coder",
            modelId = "llama3-70b-8192",
            systemPrompt = "You are an expert Kotlin/Jetpack Compose developer. Write clean, compilable code for the given plan. Prefer Material 3 and modern Android patterns.",
        ),
        SwarmAgent(
            name = "Critic",
            role = "Reviewer",
            modelId = "mixtral-8x7b-32768",
            systemPrompt = "You are a strict code reviewer. Find bugs, missing imports, state issues, and suggest concrete fixes. Be concise.",
        ),
    )
}
