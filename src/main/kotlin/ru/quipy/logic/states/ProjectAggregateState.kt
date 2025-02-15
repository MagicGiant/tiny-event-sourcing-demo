package ru.quipy.logic.states

import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.events.ParticipantAddedEvent
import ru.quipy.api.events.ProjectCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    lateinit var title: String
    lateinit var creatorId: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var participants = mutableMapOf<String, Participant>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun onProjectCreated(event: ProjectCreatedEvent) {
        projectId = event.projectId
        title = event.title
        creatorId = event.creatorId
    }

    @StateTransitionFunc
    fun onParticipantAdded(event: ParticipantAddedEvent) {
        participants[event.userId] = Participant(event.userId)
    }
}

data class TaskEntity(
        val id: UUID,
        val name: String
)

data class Participant(
        val userId: String,
)

@StateTransitionFunc
fun ProjectAggregateState.participantAddedApply(event: ParticipantAddedEvent) {
    participants[event.userId] = Participant(event.userId)
}
