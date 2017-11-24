package firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.*
import model.Student
import java.io.FileInputStream
import java.util.*

/**
 * Class to fetch and push data to and from Firebase Realtime Database.
 * Created on 11/6/17.
 *
 * @author Evgenii Kanivets
 */
class FirebaseClient {

    init {
        val serviceAccount = FileInputStream("codemarathon-2-firebase-adminsdk-l7nzs-4143645ad6.json")

        val options = FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://codemarathon-2.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)
    }

    fun fetchStudents(failure: (reason: String) -> Unit = {}, success: (students: Array<Student>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference(KEY_USERS)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError?) {
                print(error)
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                val students = mutableListOf<Student>()
                if (snapshot != null) {
                    for (child in snapshot.children) {
                        val student = child.getValue(Student::class.java)
                        student.id = child.key
                        students.add(student)
                    }
                }
                success(students.toTypedArray())
            }
        })
    }

    fun pushStudents(students: Array<Student>, failure: (reason: String) -> Unit = {}, success: () -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference

        val updates = mutableMapOf<String, Any>()
        updates.put(LAST_UPDATE, Date(System.currentTimeMillis()).toString())

        students.forEach {
            updates.put("$KEY_USERS/${it.id}/startRating", it.startRating)
            updates.put("$KEY_USERS/${it.id}/currentRating", it.currentRating)
            updates.put("$KEY_USERS/${it.id}/bonusRating", it.bonusRating)
            updates.put("$KEY_USERS/${it.id}/contestRating", it.contestRating)
            updates.put("$KEY_USERS/${it.id}/bonuses", it.bonuses.toList())
            updates.put("$KEY_USERS/${it.id}/solvedTasks", it.solvedTasks.toList())
            updates.put("$KEY_USERS/${it.id}/notSolvedTasks", it.notSolvedTasks.toList())
        }

        usersRef.updateChildren(updates) { error: DatabaseError?, ref: DatabaseReference ->
            if (error == null) {
                success()
            } else {
                failure(error.message)
            }
        }
    }

    companion object {
        private val KEY_USERS = "users"
        private val LAST_UPDATE = "lastUpdate"
    }

}
