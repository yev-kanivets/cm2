package statistics.model

/**
 * Data class to represent statistics of single Student.
 * Created on 12/29/17.
 *
 * @author Evgenii Kanivets
 */
data class StudentStatistics(val id: String, val diff1_20: Int, val diff21_40: Int,
                             val diff41_60: Int, val diff61_80: Int, val diff81_100: Int)
