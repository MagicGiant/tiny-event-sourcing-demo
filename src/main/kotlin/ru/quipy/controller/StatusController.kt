package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.aggregates.StatusAggregate
import ru.quipy.api.events.StatusColorUpdatedEvent
import ru.quipy.api.events.StatusCreatedEvent
import ru.quipy.api.events.StatusTitleUpdatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.commands.createStatus
import ru.quipy.logic.commands.updateColor
import ru.quipy.logic.commands.updateTitle
import ru.quipy.logic.states.StatusAggregateState
import ru.quipy.projection.StatusProjection
import ru.quipy.projection.StatusProjectionRepository
import java.util.*

@RestController
@RequestMapping("/statuses")
class StatusController(
    private val statusEsService: EventSourcingService<UUID, StatusAggregate, StatusAggregateState>,
    private val statusProjectionRepository: StatusProjectionRepository
) {

    @PostMapping("/create")
    fun createStatus(@RequestBody request: CreateStatusRequest): StatusCreatedEvent {
        if (!isValidColor(request.color)) {
            throw IllegalArgumentException("Invalid color format: ${request.color}")
        }

        val event = statusEsService.create {
            it.createStatus(
                    name = request.name,
                    color = request.color
            )
        }

        val statusProjection = StatusProjection(
                statusId = event.statusId,
                title = event.title,
                color = event.color
        )

        statusProjectionRepository.save(statusProjection)

        return event
    }

    @PostMapping("/updateTitle")
    fun updateTitle(@RequestBody request: UpdateStatusTitleRequest): StatusTitleUpdatedEvent {
        val statusProjection = statusProjectionRepository.findById(request.statusId)
                .orElseThrow { NoSuchElementException("Status with ID ${request.statusId} not found") }

        val event = statusEsService.update(request.statusId) {
            it.updateTitle(request.statusId, request.newTitle)
        }

        statusProjection.title = request.newTitle
        statusProjectionRepository.save(statusProjection)

        return event
    }

    @PostMapping("/updateColor")
    fun updateColor(@RequestBody request: UpdateStatusColorRequest): StatusColorUpdatedEvent {
        val statusProjection = statusProjectionRepository.findById(request.statusId)
                .orElseThrow { NoSuchElementException("Status with ID ${request.statusId} not found") }

        if (!isValidColor(request.newColor)) {
            throw IllegalArgumentException("Invalid color format: ${request.newColor}")
        }

        val event = statusEsService.update(request.statusId) {
            it.updateColor(request.statusId, request.newColor)
        }

        statusProjection.color = request.newColor
        statusProjectionRepository.save(statusProjection)

        return event
    }

    @GetMapping("/{statusId}")
    fun getStatusById(@PathVariable statusId: UUID): StatusProjection {
        return statusProjectionRepository.findById(statusId)
                .orElseThrow { NoSuchElementException("Status with ID $statusId not found") }
    }

    @GetMapping("/all")
    fun getAllStatuses(): List<StatusProjection> {
        val statuses = statusProjectionRepository.findAll()
        if (statuses.isEmpty()) {
            throw NoSuchElementException("No statuses found")
        }
        return statuses
    }
}

data class CreateStatusRequest(
        val name: String,
        val color: String
)

data class UpdateStatusTitleRequest(
        val statusId: UUID,
        val newTitle: String
)

data class UpdateStatusColorRequest(
        val statusId: UUID,
        val newColor: String
)

fun isValidColor(color: String): Boolean {
    val colorRegex = "^#[0-9A-Fa-f]{6}$"
    return color.matches(colorRegex.toRegex())
}
