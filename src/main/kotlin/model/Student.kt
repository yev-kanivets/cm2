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
                   var startRating: Int = -1,
                   var currentRating: Int = -1,
                   var bonusRating: Int = 0,
                   var contestRating: Int = -1,
                   var bonuses: List<Bonus> = listOf(),
                   var solvedTasks: List<String> = listOf(),
                   var notSolvedTasks: List<String> = listOf())

data class Bonus(val description: String = "",
                 val value: Int = 0)