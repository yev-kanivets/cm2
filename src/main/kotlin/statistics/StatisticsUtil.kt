package statistics

import model.Student
import statistics.model.Statistics
import statistics.model.StudentStatistics
import storage.TasksStorage

/**
 * Util class to calculate statistics.
 * Created on 12/29/17.
 *
 * @author Evgenii Kanivets
 */
object StatisticsUtil {

    fun calculateStatistics(students: List<Student>): Statistics {
        var totalStartRating = 0
        var totalCurrentRating = 0
        var totalBonusRating = 0
        var totalContestRating = 0
        var studentStatistics = listOf<StudentStatistics>()

        students.forEach {
            totalStartRating += it.startRating
            totalCurrentRating += it.currentRating
            totalBonusRating += it.bonusRating
            totalContestRating += it.contestRating
            studentStatistics += calculateStudentStatistics(it)
        }

        return Statistics(totalStartRating, totalCurrentRating, totalBonusRating, totalContestRating, studentStatistics)
    }

    @Suppress("LocalVariableName")
    private fun calculateStudentStatistics(student: Student): StudentStatistics {
        var diff1_20 = 0
        var diff21_40 = 0
        var diff41_60 = 0
        var diff61_80 = 0
        var diff81_100 = 0

        student.solvedTasks.forEach {
            val task = TasksStorage.instance.tasks[it.toInt()]
            if (task != null) {
                when (task.difficulty) {
                    in 1..20 -> diff1_20++
                    in 21..40 -> diff21_40++
                    in 41..60 -> diff41_60++
                    in 61..80 -> diff61_80++
                    in 81..100 -> diff81_100++
                }
            }
        }

        return StudentStatistics(student.id, diff1_20, diff21_40, diff41_60, diff61_80, diff81_100)
    }

}
