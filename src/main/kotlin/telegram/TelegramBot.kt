package telegram

import diff.StudentDiff
import retrofit2.Retrofit
import telegram.retrofit.TelegramService

/**
 * Singleton. Client class to send messages to CodeMarathon 2.0 chats.
 * Created on 11/26/17.
 *
 * @author Evgenii Kanivets
 */
class TelegramBot private constructor() {

    private object Holder {
        val INSTANCE = TelegramBot()
    }

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://api.telegram.org/bot/") // Insert Telegram Bot token here
                .build()
    }

    private val telegramService by lazy { retrofit.create(TelegramService::class.java) }

    fun sendStudentsDiff(studentsDiff: List<StudentDiff>) {
        studentsDiff.forEach { if (it.isDiff) sendDiff(it) }
    }

    private fun sendDiff(studentDiff: StudentDiff) {
        if (studentDiff.currentRatingDiff.isDiff && studentDiff.solvedTasksDiff.diff.isNotEmpty()) {
            val sb = StringBuilder()
            sb.append("+${studentDiff.currentRatingDiff.diff} рейтинга ${studentDiff.solvedTasksDiff.diff} ")
            sb.append(studentDiff.new.fullname)

            val call = telegramService.sendMessage(getChatId(studentDiff.new.division), sb.toString())
            val response = call.execute()
            println(response.isSuccessful)
        }
    }

    private fun getChatId(division: String): Long {
        when (division) {
            "Div1" -> return -1001274013445
            "Div2" -> return -1001159648363
            "Div3" -> return -1001294451775
        }
        return -1
    }

    companion object {
        val instance: TelegramBot by lazy { Holder.INSTANCE }
    }

}