package firebase

import acmp.AcmpTask
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.*
import model.Student
import statistics.model.Statistics
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
        val serviceAccount = FileInputStream("codemarathon-2-dev-firebase-adminsdk.json")

        val options = FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://codemarathon-2-dev.firebaseio.com")
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

    fun pushTasks(tasks: Array<AcmpTask>, failure: (reason: String) -> Unit = {}, success: () -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.reference

        val updates = mutableMapOf<String, Any>()
        updates.putAll(getTasks(tasks))

        ref.updateChildren(updates) { error: DatabaseError?, dbRef: DatabaseReference ->
            if (error == null) {
                success()
            } else {
                failure(error.message)
            }
        }
    }

    fun pushStudentsAndStatistics(students: Array<Student>, statistics: Statistics,
                                  failure: (reason: String) -> Unit = {}, success: () -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.reference

        val updates = mutableMapOf<String, Any>()
        updates.put(KEY_LAST_UPDATE, Date(System.currentTimeMillis()).toString())
        updates.putAll(getStudentsUpdates(students))
        updates.putAll(getStatisticsUpdates(statistics))

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

    private fun getTasks(tasks: Array<AcmpTask>): Map<String, Any> {
        val updates = mutableMapOf<String, Any>()

        tasks.forEach {
            updates.put("$KEY_TASKS/${it.id}/title", it.title)
            updates.put("$KEY_TASKS/${it.id}/topic", it.topic)
            updates.put("$KEY_TASKS/${it.id}/analysis", it.analysis ?: "Нет")
            updates.put("$KEY_TASKS/${it.id}/difficulty", it.difficulty)
            updates.put("$KEY_TASKS/${it.id}/solvency", it.solvency)
            updates.put("$KEY_TASKS/${it.id}/accepted", it.accepted)
        }

        return updates
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

    private fun getStatisticsUpdates(statistics: Statistics): Map<String, Any> {
        val updates = mutableMapOf<String, Any>()

        updates.put("$KEY_STATISTICS/totalStartRating", statistics.totalStartRating)
        updates.put("$KEY_STATISTICS/totalCurrentRating", statistics.totalCurrentRating)
        updates.put("$KEY_STATISTICS/totalBonusRating", statistics.totalBonusRating)
        updates.put("$KEY_STATISTICS/totalContestRating", statistics.totalContestRating)

        statistics.studentStatistics.forEach {
            updates.put("$KEY_STATISTICS/studentStatistics/${it.id}/diff1_20", it.diff1_20)
            updates.put("$KEY_STATISTICS/studentStatistics/${it.id}/diff21_40", it.diff21_40)
            updates.put("$KEY_STATISTICS/studentStatistics/${it.id}/diff41_60", it.diff41_60)
            updates.put("$KEY_STATISTICS/studentStatistics/${it.id}/diff61_80", it.diff61_80)
            updates.put("$KEY_STATISTICS/studentStatistics/${it.id}/diff81_100", it.diff81_100)
        }

        return updates
    }

    companion object {
        private val KEY_TASKS = "tasks"
        private val KEY_USERS = "users"
        private val KEY_LAST_UPDATE = "lastUpdate"
        private val KEY_BACKUPS = "backups"
        private val KEY_STATISTICS = "statistics"
    }

}
