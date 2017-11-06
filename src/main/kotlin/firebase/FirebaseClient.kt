package firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import model.Student
import java.io.FileInputStream

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

    public fun fetchStudents(failure: (reason: String) -> Unit = {}, success: (students: Array<Student>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference(KEY_USERS)
        usersRef.addValueEventListener(object : ValueEventListener {
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

    companion object {
        private val KEY_USERS = "users"
    }

}
