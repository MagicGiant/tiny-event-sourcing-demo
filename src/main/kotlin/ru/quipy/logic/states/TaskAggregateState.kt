package ru.quipy.logic.states

import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.api.events.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.time.LocalDateTime
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    lateinit var id: UUID
    lateinit var linkedProjectId: UUID
    lateinit var description: String
    lateinit var currentStatusId: UUID
    lateinit var title: String
    var deadlineTimestamp: LocalDateTime? = null
    var taskExecutors = mutableMapOf<UUID, TaskExecutor>()

    override fun getId() = id

    @StateTransitionFunc
    fun onTaskCreated(event: TaskCreatedEvent) {
        id = event.taskId
        linkedProjectId = event.projectId
        title = event.title
        description = event.description
        deadlineTimestamp = event.deadlineTimestamp
        currentStatusId = event.statusId
        taskExecutors.putAll(event.executors)
    }

    @StateTransitionFunc
    fun onTaskTitleUpdated(event: TaskTitleUpdatedEvent) {
        title = event.newTitle
    }

    @StateTransitionFunc
    fun onTaskDescriptionUpdated(event: TaskDescriptionUpdatedEvent) {
        description = event.newDescription
    }

    @StateTransitionFunc
    fun onExecutorAdded(event: TaskExecutorAddedEvent) {
        taskExecutors[event.executorId] = TaskExecutor(event.executorId)
    }

    @StateTransitionFunc
    fun onStatusUpdated(event: TaskStatusUpdatedEvent) {
        currentStatusId = event.newStatusId
    }

    @StateTransitionFunc
    fun onTaskDeleted(event: TaskDeletedEvent) {
        taskExecutors.clear()
    }
}

data class TaskExecutor(
        val userId: UUID

)
