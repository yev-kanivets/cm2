package statistics.model

/**
 * Data class to represent Statistics.
 * Created on 12/29/17.
 *
 * @author Evgenii Kanivets
 */
data class Statistics(val totalStartRating: Int, val totalCurrentRating: Int, val totalBonusRating: Int,
                      val totalContestRating: Int, val studentStatistics: List<StudentStatistics>)