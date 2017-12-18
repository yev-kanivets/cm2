import acmp.AcmpClient
import diff.StudentDiff
import firebase.FirebaseClient
import model.Student
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import storage.TasksStorage
import telegram.TelegramBot
import java.util.*

/**
 * Application entry point.
 * Created on 11/6/17.
 *
 * @author Evgenii Kanivets
 */

val firebaseClient = FirebaseClient()
val acmpClient = AcmpClient()

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    Logger.getRootLogger().level = Level.ERROR

    val checkPeriod = if (args.size == 1) args[0].toLong() else 600000

    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            if (TasksStorage.instance.tasks.isEmpty()) {
                TasksStorage.instance.tasks = acmpClient.fetchTasks().map { it.id to it }.toMap()
                println("${TasksStorage.instance.tasks.size} tasks fetched from ACMP")
            }
            fetchStudents { oldStudents, newStudents ->
                sendDiffs(oldStudents, newStudents)

                firebaseClient.pushStudents(newStudents) {
                    println("${Date(System.currentTimeMillis())} Students pushed to Firebase")
                    firebaseClient.pushBackup(newStudents) {
                        println("Backup pushed to Firebase")
                    }
                }
            }
        }
    }, 0, checkPeriod)
}

private fun fetchStudents(failure: (reason: String) -> Unit = {},
                          success: (oldStudents: Array<Student>, newStudents: Array<Student>) -> Unit) {
    firebaseClient.fetchStudents { oldStudents ->
        val newStudents = mutableListOf<Student>()
        oldStudents.forEach { newStudents.add(it.copy()) }

        acmpClient.fetchStudents(newStudents.map { it.acmpId }.toTypedArray()) { acmpUsers ->
            newStudents.forEach {
                val student = it
                val acmpUser = acmpUsers.find { it.acmpId == student.acmpId }

                if (acmpUser != null) {
                    student.currentRating = acmpUser.currentRating
                    if (student.startRating == -1) {
                        student.startRating = student.currentRating
                    }

                    student.bonusRating = 0
                    student.bonuses.forEach { student.bonusRating += it.value }

                    student.contestRating = student.currentRating - student.startRating + student.bonusRating
                    student.solvedTasks = acmpUser.solvedTasks.toList()
                    student.notSolvedTasks = acmpUser.notSolvedTasks.toList()
                }
            }

            success(oldStudents, newStudents.toTypedArray())
        }
    }
}

private fun sendDiffs(oldStudents: Array<Student>, newStudents: Array<Student>) {
    val studentsDiff = mutableListOf<StudentDiff>()
    newStudents.forEachIndexed { i, student ->
        studentsDiff.add(StudentDiff(oldStudents[i], student))
    }
    TelegramBot.instance.sendStudentsDiff(studentsDiff)
}
