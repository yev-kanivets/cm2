import acmp.AcmpClient
import firebase.FirebaseClient
import model.Student
import org.apache.log4j.BasicConfigurator
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

    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            fetchStudents {
                firebaseClient.pushStudents(it) {
                    print("Students pushed to Firebase")
                }
            }
        }
    }, 0, 600000)
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
                    student.solvedTasks = acmpUser.solvedTasks
                    student.notSolvedTasks = acmpUser.notSolvedTasks
                }
            }
            success(students)
        }
    }
}
