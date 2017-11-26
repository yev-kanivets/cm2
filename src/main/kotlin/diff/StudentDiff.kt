package diff

import model.Student

/**
 * Class to represent a difference between Student entities. It also encapsulates a logic.
 * Created on 11/26/17.
 *
 * @author Evgenii Kanivets
 */
data class StudentDiff(private val old: Student, private val new: Student) {

    val currentRatingDiff by lazy { Diff(old.currentRating, new.currentRating, new.currentRating - old.currentRating) }

    val bonusRatingDiff by lazy { Diff(old.bonusRating, new.bonusRating, new.bonusRating - old.bonusRating) }

    val contestRatingDiff by lazy { Diff(old.contestRating, new.contestRating, new.contestRating - old.contestRating) }

    val bonusesDiff by lazy { Diff(old.bonuses, new.bonuses, new.bonuses - old.bonuses) }

    val solvedTasksDiff by lazy { Diff(old.solvedTasks, new.solvedTasks, new.solvedTasks - old.solvedTasks) }

    val notSolvedTasksDiff by lazy { Diff(old.notSolvedTasks, new.notSolvedTasks, new.notSolvedTasks - old.notSolvedTasks) }

    val isDiff: Boolean
        get() = currentRatingDiff.isDiff || bonusRatingDiff.isDiff || contestRatingDiff.isDiff
                || bonusesDiff.isDiff || solvedTasksDiff.isDiff || notSolvedTasksDiff.isDiff

    val isNotDiff: Boolean
        get() = currentRatingDiff.isNotDiff && bonusRatingDiff.isNotDiff && contestRatingDiff.isNotDiff
                && bonusesDiff.isNotDiff && solvedTasksDiff.isNotDiff && notSolvedTasksDiff.isNotDiff

    override fun toString(): String {
        val sb = StringBuilder()

        sb.append("${old.fullname} (${old.acmpId}) {\n")
        if (currentRatingDiff.isDiff) {
            sb.append("\tcurrentRatingDiff ${currentRatingDiff.diff}\n")
        }
        if (bonusRatingDiff.isDiff) {
            sb.append("\tbonusRatingDiff ${bonusRatingDiff.diff}\n")
        }
        if (contestRatingDiff.isDiff) {
            sb.append("\tcontestRatingDiff ${contestRatingDiff.diff}\n")
        }
        if (bonusesDiff.isDiff) {
            sb.append("\tbonusesDiff ${bonusesDiff.diff}\n")
        }
        if (solvedTasksDiff.isDiff) {
            sb.append("\tsolvedTasksDiff ${solvedTasksDiff.diff}\n")
        }
        if (notSolvedTasksDiff.isDiff) {
            sb.append("\tnotSolvedTasksDiff ${notSolvedTasksDiff.diff}\n")
        }
        sb.append("}")

        return sb.toString()
    }

}
