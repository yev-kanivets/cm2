package diff

/**
 * Data class to represent a difference between any objects.
 * Created on 11/26/17.
 *
 * @author Evgenii Kanivets
 */
data class Diff<out T>(val old: T, val new: T, val diff: T) {

    val isDiff: Boolean
        get() = old != new

    val isNotDiff: Boolean
        get() = old == new

}
