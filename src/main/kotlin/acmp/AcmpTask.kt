package acmp

/**
 * Data class to represent ACMP task.
 * Created on 12/16/17.
 *
 * @author Evgenii Kanivets
 */
data class AcmpTask(val id: Int, val title: String, val topic: String, val analysis: String?,
                    val difficulty: Int, val solvency: Int, val accepted: Int)
