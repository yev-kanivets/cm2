package acmp

import model.Student
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.regex.Pattern

/**
 * Class to fetch data from acmp.ru.
 * Created on 11/6/17.
 *
 * @author Evgenii Kanivets
 */
class AcmpClient {

    public fun fetchStudents(studentIds: Array<String>, failure: (reason: String) -> Unit = {},
                             success: (students: Array<AcmpUser>) -> Unit) {
        val acmpUsers = mutableListOf<AcmpUser>()

        studentIds.forEach {
            val document: Document
            try {
                document = Jsoup.connect(USER_URL + it).timeout(15000).get()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }

            val pattern = Pattern.compile(START_HEADER + ".*" + END_HEADER)
            val matcher = pattern.matcher(document.body().toString())
            var statistics: String? = null
            if (matcher.find()) statistics = matcher.group()

            var currentRating = -1
            val solvedTasks = mutableListOf<String>()
            val notSolvedTasks = mutableListOf<String>()

            if (statistics != null) {
                val statisticsDocument = Jsoup.parse(statistics)

                val elementsByTagB = statisticsDocument.getElementsByTag("b")
                if (elementsByTagB.size >= 2) {
                    val placeElement = elementsByTagB.get(0)
                    val ratingElement = elementsByTagB.get(1)

                    val rating = ratingElement.text().substring(8)
                    val words = rating.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

                    if (words.size == 2) {
                        currentRating = Integer.parseInt(words[0].trim({ it <= ' ' }))
                    }
                }

                val elementsByTagP = statisticsDocument.getElementsByTag("p")
                if (elementsByTagP.size === 2) {
                    // Solved tasks
                    elementsByTagP[0].getElementsByTag("a").mapTo(solvedTasks) { it.attr("href").substringAfterLast("=") }

                    // Not solved tasks
                    elementsByTagP[1].getElementsByTag("a").mapTo(notSolvedTasks) { it.attr("href").substringAfterLast("=") }
                }
            }

            val acmpUser = AcmpUser(it, currentRating, solvedTasks.toTypedArray(), notSolvedTasks.toTypedArray())
            acmpUsers.add(acmpUser)
        }

        success(acmpUsers.toTypedArray())
    }

    companion object {
        private val BASE_URL = "http://www.acmp.ru/"
        private val USER_URL = BASE_URL + "?main=user&id="
        private val START_HEADER = "<h4>Общая статистика</h4>"
        private val END_HEADER = "<h4>Статистика раздела &quot;Курсы&quot;</h4>"
    }

}