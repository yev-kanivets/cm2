import acmp.AcmpClient
import firebase.FirebaseClient
import model.Student
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
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
            fetchStudents {
                firebaseClient.pushStudents(it) {
                    println("${Date(System.currentTimeMillis())} Students pushed to Firebase")
                    firebaseClient.pushBackup(it) {
                        println("Backup pushed to Firebase")
                    }
                }
            }
        }
    }, 0, checkPeriod)
}

fun fetchStudents(failure: (reason: String) -> Unit = {}, success: (students: Array<Student>) -> Unit) {
    firebaseClient.fetchStudents { students ->
        acmpClient.fetchStudents(students.map { it.acmpId }.toTypedArray()) { acmpUsers ->
            students.forEach {
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
            success(students)
        }
    }
}
