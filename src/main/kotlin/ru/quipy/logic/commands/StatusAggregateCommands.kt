package ru.quipy.logic.commands

import ru.quipy.api.events.StatusColorUpdatedEvent
import ru.quipy.api.events.StatusCreatedEvent
import ru.quipy.api.events.StatusTitleUpdatedEvent
import ru.quipy.logic.states.StatusAggregateState
import java.util.*

fun StatusAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    val statusId = UUID.randomUUID()
    return StatusCreatedEvent(
            statusId = statusId,
            title = name,
            color = color
    )
}

fun StatusAggregateState.updateTitle(statusId: UUID, newTitle: String): StatusTitleUpdatedEvent {
    return StatusTitleUpdatedEvent(
            statusId = statusId,
            newTitle = newTitle
    )
}

fun StatusAggregateState.updateColor(statusId: UUID, newColor: String): StatusColorUpdatedEvent {
    return StatusColorUpdatedEvent(
            statusId = statusId,
            newColor = newColor
    )
}
