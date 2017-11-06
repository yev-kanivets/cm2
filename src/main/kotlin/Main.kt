import firebase.FirebaseClient
import org.apache.log4j.BasicConfigurator

/**
 * Application entry point.
 * Created on 11/6/17.
 *
 * @author Evgenii Kanivets
 */
fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val firebaseClient = FirebaseClient()

    firebaseClient.fetchStudents { students ->
        students.forEach(::println)
    }

    while (true) {

    }
}
