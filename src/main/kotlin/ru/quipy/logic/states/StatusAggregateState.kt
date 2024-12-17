package ru.quipy.logic.states

import ru.quipy.api.aggregates.StatusAggregate
import ru.quipy.api.events.StatusColorUpdatedEvent
import ru.quipy.api.events.StatusCreatedEvent
import ru.quipy.api.events.StatusTitleUpdatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class StatusAggregateState : AggregateState<UUID, StatusAggregate> {
    lateinit var statusId: UUID
    lateinit var title: String
    lateinit var color: String

    override fun getId() = statusId

    @StateTransitionFunc
    fun onStatusCreated(event: StatusCreatedEvent) {
        statusId = event.statusId
        title = event.title
        color = event.color
    }

    @StateTransitionFunc
    fun onStatusTitleUpdated(event: StatusTitleUpdatedEvent) {
        title = event.newTitle
    }

    @StateTransitionFunc
    fun onStatusColorUpdated(event: StatusColorUpdatedEvent) {
        color = event.newColor
    }
}
