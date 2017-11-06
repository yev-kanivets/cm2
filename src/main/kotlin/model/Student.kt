package model

/**
 * Data class to represent Student.
 * Created on 11/6/17.
 *
 * @author Evgenii Kanivets
 */
data class Student(var id: String = "",
                   val acmpId: String = "",
                   val dateTime: String = "",
                   val division: String = "",
                   val email: String = "",
                   val fullname: String = "",
                   val telegramUsername: String = "",
                   val startRating: Int = -1,
                   val currentRating: Int = -1)