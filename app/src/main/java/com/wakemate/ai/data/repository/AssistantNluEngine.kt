package com.wakemate.ai.data.repository

import com.wakemate.ai.data.local.entity.TaskEntity
import java.util.regex.Pattern

data class NluResponse(
    val replyText: String,
    val createdTask: TaskEntity? = null,
    val isAwakeConfirmed: Boolean = false,
    val isSleepyReported: Boolean = false,
    val quickReplies: List<String> = emptyList()
)

class AssistantNluEngine {

    fun processMessage(
        userMessage: String,
        pendingTasks: List<TaskEntity>,
        personality: String = "FRIENDLY",
        languagePreference: String = "en"
    ): NluResponse {
        val trimmed = userMessage.trim()
        val lower = trimmed.lowercase()

        // 1. Check for Awake Confirmation
        if (isAwakeMessage(lower)) {
            val reply = when {
                isRomanUrdu(lower) -> "Zabardast! Main khush hoon ke aap jaag gaye hain. Aaj aap ke paas ${pendingTasks.size} tasks hain. Chalo din shuru karte hain! 🚀"
                isUrdu(lower) -> "بہت خوب! خوشی ہوئی کہ آپ بیدار ہو گئے۔ آج آپ کے پاس ${pendingTasks.size} کام باقی ہیں۔ آئیے دن کا آغاز کرتے ہیں! 🚀"
                else -> "Awesome! So glad to see you're wide awake. You have ${pendingTasks.size} task(s) planned for today. Let's make it productive! ☀️"
            }
            return NluResponse(
                replyText = reply,
                isAwakeConfirmed = true,
                quickReplies = listOf("Show pending tasks 📋", "Add new task ➕", "Set an alarm ⏰")
            )
        }

        // 2. Check for Sleepy / Still Sleeping
        if (isSleepyMessage(lower)) {
            val reply = when {
                isRomanUrdu(lower) -> "Utho dost! Thora paani piyo aur thandi hawa lo. Aaj bohat kuch achieve karna hai! 💪"
                isUrdu(lower) -> "اٹھو میرے دوست! تھوڑا پانی پیو اور چست ہو جاؤ۔ آج بہت کچھ حاصل کرنا ہے! 💪"
                else -> "Shake it off! Drink a cold glass of water and stretch. Great things don't happen in bed! Let's get up! ⚡"
            }
            return NluResponse(
                replyText = reply,
                isSleepyReported = true,
                quickReplies = listOf("Yes, I'm awake ✅", "Give me 5 mins")
            )
        }

        // 3. Task Creation: Roman Urdu ("Mujhe 6 ghante baad study yaad dilana")
        val romanUrduTaskMatch = matchRomanUrduTask(trimmed)
        if (romanUrduTaskMatch != null) {
            val (taskTitle, hours) = romanUrduTaskMatch
            val dueMillis = System.currentTimeMillis() + (hours * 3600 * 1000L)
            val newTask = TaskEntity(
                title = taskTitle.replaceFirstChar { it.uppercase() },
                description = "Created automatically via assistant chat",
                dueDateTimeMillis = dueMillis,
                priority = if (hours <= 2) "HIGH" else "MEDIUM",
                category = detectCategory(taskTitle),
                reminderIntervalHours = if (hours <= 3) 1 else 6
            )
            val reply = "Done! Main tumhe $hours ghante baad '$taskTitle' ka reminder dunga. 📚"
            return NluResponse(
                replyText = reply,
                createdTask = newTask,
                quickReplies = listOf("Show pending tasks 📋", "Add another task ➕")
            )
        }

        // 4. Task Creation: English ("Remind me to study in 6 hours" / "Add task exercise in 2 hours")
        val englishTaskMatch = matchEnglishTask(trimmed)
        if (englishTaskMatch != null) {
            val (taskTitle, hours) = englishTaskMatch
            val dueMillis = System.currentTimeMillis() + (hours * 3600 * 1000L)
            val newTask = TaskEntity(
                title = taskTitle.replaceFirstChar { it.uppercase() },
                description = "Created automatically via assistant chat",
                dueDateTimeMillis = dueMillis,
                priority = if (hours <= 2) "HIGH" else "MEDIUM",
                category = detectCategory(taskTitle),
                reminderIntervalHours = if (hours <= 3) 1 else 6
            )
            val reply = "Got it! I scheduled a reminder for '$taskTitle' in $hours hour(s). 🎯"
            return NluResponse(
                replyText = reply,
                createdTask = newTask,
                quickReplies = listOf("Show pending tasks 📋", "Add another task ➕")
            )
        }

        // 5. Query Pending Tasks
        if (isTaskQuery(lower)) {
            if (pendingTasks.isEmpty()) {
                val reply = when {
                    isRomanUrdu(lower) -> "Aap ke paas filhaal koi pending task nahi hai! Mazay karo ya naya task add karo. 🎉"
                    else -> "You have no pending tasks! You are all caught up. 🎉"
                }
                return NluResponse(replyText = reply, quickReplies = listOf("Add new task ➕"))
            } else {
                val taskListStr = pendingTasks.take(4).mapIndexed { i, t -> "${i + 1}. ${t.title} (${t.priority})" }.joinToString("\n")
                val reply = when {
                    isRomanUrdu(lower) -> "Aap ke ${pendingTasks.size} pending tasks hain:\n$taskListStr"
                    else -> "You have ${pendingTasks.size} pending task(s):\n$taskListStr"
                }
                return NluResponse(replyText = reply, quickReplies = listOf("Add new task ➕", "Clear tasks"))
            }
        }

        // 6. Greetings & Casual Conversation
        if (isGreeting(lower)) {
            val reply = when {
                isRomanUrdu(lower) -> "Assalam-o-Alaikum! Main WakeMate hoon. Aaj main aap ki kya madad kar sakta hoon? 😊"
                isUrdu(lower) -> "السلام علیکم! میں ویک میٹ ہوں۔ آج میں آپ کی کیا مدد کر سکتا ہوں؟ 😊"
                else -> {
                    when (personality) {
                        "MOTIVATIONAL" -> "Rise and shine champion! 🌟 Every second is a fresh opportunity to crush your goals. What are we tackling first?"
                        "STRICT" -> "Attention! Time is ticking. No procrastination today. Review your tasks and get to work!"
                        else -> "Good morning! ☀️ I hope you had a restful sleep. You have ${pendingTasks.size} task(s) on your agenda today. How can I assist you?"
                    }
                }
            }
            return NluResponse(
                replyText = reply,
                quickReplies = listOf("Show pending tasks 📋", "Add new task ➕", "Yes, I'm awake ✅")
            )
        }

        // 7. General Fallback
        val fallback = when {
            isRomanUrdu(lower) -> "Samajh gaya! Aap mujh se task create karwa sakte hain (maslan: 'Mujhe 3 ghante baad study yaad dilana') ya alarms set karwa sakte hain."
            isUrdu(lower) -> "سمجھ گیا! آپ مجھ سے کام یاد دلوانے کا کہہ سکتے ہیں یا الارم لگا سکتے ہیں۔"
            else -> "I hear you! You can ask me to remind you about tasks (e.g., 'Remind me to drink water in 2 hours') or ask for your daily schedule."
        }

        return NluResponse(
            replyText = fallback,
            quickReplies = listOf("Show pending tasks 📋", "Remind me to study in 2 hours")
        )
    }

    private fun isAwakeMessage(text: String): Boolean {
        return text.contains("yes") ||
                text.contains("awake") ||
                text.contains("haan") ||
                text.contains("ji haan") ||
                text.contains("jaag") ||
                text.contains("bain uth") ||
                text.contains("بیدار") ||
                text.contains("ہاں")
    }

    private fun isSleepyMessage(text: String): Boolean {
        return text.contains("sleepy") ||
                text.contains("no") ||
                text.contains("nahi") ||
                text.contains("sone do") ||
                text.contains("neend") ||
                text.contains("تھک") ||
                text.contains("نیند")
    }

    private fun isGreeting(text: String): Boolean {
        return text.contains("hello") ||
                text.contains("hi") ||
                text.contains("hey") ||
                text.contains("morning") ||
                text.contains("salam") ||
                text.contains("kese ho") ||
                text.contains("kaise ho") ||
                text.contains("سلام")
    }

    private fun isTaskQuery(text: String): Boolean {
        return text.contains("task") ||
                text.contains("kaam") ||
                text.contains("pending") ||
                text.contains("schedule") ||
                text.contains("کام")
    }

    private fun isRomanUrdu(text: String): Boolean {
        return text.contains("mujhe") ||
                text.contains("ghante") ||
                text.contains("baad") ||
                text.contains("yaad") ||
                text.contains("karo") ||
                text.contains("dilana") ||
                text.contains("kya") ||
                text.contains("hai") ||
                text.contains("hoon")
    }

    private fun isUrdu(text: String): Boolean {
        return text.any { it in '\u0600'..'\u06FF' }
    }

    private fun matchRomanUrduTask(text: String): Pair<String, Int>? {
        // e.g. "Mujhe 6 ghante baad study yaad dilana" or "6 ghante baad gym ka reminder"
        val pattern1 = Pattern.compile("(?i)mujhe\\s+(\\d+)\\s*ghant[ea]\\s+baad\\s+(.*?)(?:\\s+yaad|\\s+reminder|$)", Pattern.CASE_INSENSITIVE)
        val m1 = pattern1.matcher(text)
        if (m1.find()) {
            val hours = m1.group(1)?.toIntOrNull() ?: 1
            val task = m1.group(2)?.trim() ?: "Task"
            return Pair(cleanTaskTitle(task), hours)
        }

        val pattern2 = Pattern.compile("(?i)(\\d+)\\s*ghant[ea]\\s+baad\\s+(.*)", Pattern.CASE_INSENSITIVE)
        val m2 = pattern2.matcher(text)
        if (m2.find()) {
            val hours = m2.group(1)?.toIntOrNull() ?: 1
            var task = m2.group(2)?.trim() ?: "Task"
            task = task.replace("yaad dilana", "").replace("reminder", "").trim()
            return Pair(cleanTaskTitle(task), hours)
        }
        return null
    }

    private fun matchEnglishTask(text: String): Pair<String, Int>? {
        // e.g. "Remind me to study in 6 hours"
        val pattern1 = Pattern.compile("(?i)remind me to\\s+(.*?)\\s+in\\s+(\\d+)\\s*h(?:our|rs)?", Pattern.CASE_INSENSITIVE)
        val m1 = pattern1.matcher(text)
        if (m1.find()) {
            val task = m1.group(1)?.trim() ?: "Task"
            val hours = m1.group(2)?.toIntOrNull() ?: 1
            return Pair(cleanTaskTitle(task), hours)
        }

        // e.g. "Add task workout in 3 hours"
        val pattern2 = Pattern.compile("(?i)(?:add task|new task)\\s+(.*?)\\s+in\\s+(\\d+)\\s*h(?:our|rs)?", Pattern.CASE_INSENSITIVE)
        val m2 = pattern2.matcher(text)
        if (m2.find()) {
            val task = m2.group(1)?.trim() ?: "Task"
            val hours = m2.group(2)?.toIntOrNull() ?: 1
            return Pair(cleanTaskTitle(task), hours)
        }

        return null
    }

    private fun cleanTaskTitle(raw: String): String {
        return raw.replace("karna hai", "")
            .replace("dilana", "")
            .replace("yaad", "")
            .replace("ka reminder", "")
            .trim()
            .ifEmpty { "Important Task" }
    }

    private fun detectCategory(title: String): String {
        val lower = title.lowercase()
        return when {
            lower.contains("study") || lower.contains("read") || lower.contains("homework") || lower.contains("exam") || lower.contains("code") || lower.contains("programming") -> "Study"
            lower.contains("exercise") || lower.contains("gym") || lower.contains("water") || lower.contains("walk") || lower.contains("health") -> "Health"
            lower.contains("meeting") || lower.contains("office") || lower.contains("client") || lower.contains("work") || lower.contains("project") -> "Work"
            else -> "Personal"
        }
    }
}
