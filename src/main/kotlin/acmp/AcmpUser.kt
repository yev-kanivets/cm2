package acmp

/**
 * Data class to represent ACMP user.
 * Created on 11/6/17.
 *
 * @author Evgenii Kanivets
 */
data class AcmpUser(val acmpId: String, val currentRating: Int,
                    val solvedTasks: Array<String> = arrayOf<String>(),
                    val notSolvedTasks: Array<String> = arrayOf<String>())