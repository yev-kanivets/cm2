package firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.*
import model.Student
import java.io.FileInputStream
import java.text.SimpleDateFormat
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
                failure(error.toString())
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
        val ref = database.reference

        val updates = mutableMapOf<String, Any>()
        updates.put(KEY_LAST_UPDATE, Date(System.currentTimeMillis()).toString())
        updates.putAll(getStudentsUpdates(students))

        ref.updateChildren(updates) { error: DatabaseError?, dbRef: DatabaseReference ->
            if (error == null) {
                success()
            } else {
                failure(error.message)
            }
        }
    }

    fun pushBackup(students: Array<Student>, failure: (reason: String) -> Unit = {}, success: () -> Unit) {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy")
        val database = FirebaseDatabase.getInstance()
        val backupRef = database.getReference(KEY_BACKUPS).child(dateFormat.format(Date()))

        backupRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError?) {
                failure(error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                if (snapshot?.value == null) {
                    backupRef.updateChildren(getStudentsUpdates(students)) { error: DatabaseError?, dbRef: DatabaseReference ->
                        if (error == null) {
                            success()
                        } else {
                            failure(error.message)
                        }
                    }
                } else {
                    failure("Backup already made.")
                }
            }
        })
    }

    private fun getStudentsUpdates(students: Array<Student>): Map<String, Any> {
        val updates = mutableMapOf<String, Any>()

        students.forEach {
            updates.put("$KEY_USERS/${it.id}/startRating", it.startRating)
            updates.put("$KEY_USERS/${it.id}/currentRating", it.currentRating)
            updates.put("$KEY_USERS/${it.id}/bonusRating", it.bonusRating)
            updates.put("$KEY_USERS/${it.id}/contestRating", it.contestRating)
            updates.put("$KEY_USERS/${it.id}/bonuses", it.bonuses)
            updates.put("$KEY_USERS/${it.id}/solvedTasks", it.solvedTasks)
            updates.put("$KEY_USERS/${it.id}/notSolvedTasks", it.notSolvedTasks)
        }

        return updates
    }

    companion object {
        private val KEY_USERS = "users"
        private val KEY_LAST_UPDATE = "lastUpdate"
        private val KEY_BACKUPS = "backups"
    }

}
