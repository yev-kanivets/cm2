package storage

import acmp.AcmpTask

/**
 * Singleton. Class to store fetched ACMP tasks.
 * Created on 12/17/17.
 *
 * @author Evgenii Kanivets
 */
class TasksStorage private constructor() {

    private object Holder {
        val INSTANCE = TasksStorage()
    }

    var tasks: Map<Int, AcmpTask> = mapOf()

    companion object {
        val instance: TasksStorage by lazy { Holder.INSTANCE }
    }

}
