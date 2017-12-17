package acmp

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

    fun fetchStudents(studentIds: Array<String>, failure: (reason: String) -> Unit = {},
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
                    val placeElement = elementsByTagB[0]
                    val ratingElement = elementsByTagB[1]

                    val rating = ratingElement.text().substring(8)
                    val words = rating.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

                    if (words.size == 2) {
                        currentRating = Integer.parseInt(words[0].trim({ it <= ' ' }))
                    }
                }

                val elementsByTagP = statisticsDocument.getElementsByTag("p")
                if (elementsByTagP.size == 2) {
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

    fun fetchTasks(): List<AcmpTask> {
        val acmpTasks = mutableListOf<AcmpTask>()

        var page = 0
        while (true) {
            val pageTasks = fetchTasksPage(page)
            if (pageTasks.isEmpty()) {
                break
            } else {
                acmpTasks += pageTasks
                page += 1
            }
        }

        return acmpTasks
    }

    private fun fetchTasksPage(page: Int): List<AcmpTask> {
        val acmpTasks = mutableListOf<AcmpTask>()

        val document: Document
        try {
            document = Jsoup.connect(TASKS_URL + page).timeout(15000).get()
        } catch (e: IOException) {
            e.printStackTrace()
            return listOf()
        }

        val tableElement = document.getElementsByTag("table")[14]
        val rowElements = tableElement.getElementsByTag("tr")
        val rows = rowElements.subList(1, rowElements.size)

        rows.forEach {
            val values = it.getElementsByTag("td")
            val acmpTask = AcmpTask(id = values[0].text().toInt(), title = values[1].text(), topic = values[2].text(),
                    analysis = values[3].text(), difficulty = values[4].text().substringBefore("%").toInt(),
                    solvency = values[5].text().substringBefore("%").toInt(), accepted = values[6].text().toInt())
            acmpTasks.add(acmpTask)
        }

        return acmpTasks
    }

    companion object {
        private val BASE_URL = "http://www.acmp.ru/"
        private val USER_URL = BASE_URL + "?main=user&id="
        private val TASKS_URL = BASE_URL + "?main=tasks&str=%20&id_type=0&page="
        private val START_HEADER = "<h4>Общая статистика</h4>"
        private val END_HEADER = "<h4>Статистика раздела &quot;Курсы&quot;</h4>"
    }

}