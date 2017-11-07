import acmp.AcmpClient
import firebase.FirebaseClient
import model.Student
import org.apache.log4j.BasicConfigurator

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

    fetchStudents {
        it.forEach(::println)
    }

    while (true) {

    }
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
