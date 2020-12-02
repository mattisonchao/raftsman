package com.github.mattisonchao.dispathcher

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Use coroutine to dispatch task.
 *
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 */
interface Dispatcher {
    /**
     * Register task to task center
     *
     * @param tasks list of task
     * @see Task
     */
    fun registerTask(tasks: List<Task>)

    /**
     * Start dispatcher to run task.
     */
    fun startup()
}

/**
 * Rafter dispatcher implementation
 * @since 1.1.1
 * @see Dispatcher
 * @author mattisonchao@gmail.com
 */
class RafterNodeDispatcher : Dispatcher {
    private val taskCentre = mutableListOf<Task>()

    override fun registerTask(tasks: List<Task>) {
        taskCentre.addAll(tasks)
    }

    override fun startup() {
        GlobalScope.launch {
            taskCentre.forEach {
                launch { it.run() }
            }
        }
    }
}


