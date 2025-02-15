package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.events.ParticipantAddedEvent
import ru.quipy.api.events.ProjectCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.projection.ProjectProjection
import ru.quipy.projection.ParticipantProjection
import ru.quipy.projection.ProjectProjectionRepository
import ru.quipy.api.ProjectionsService
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.logic.commands.addParticipant
import ru.quipy.logic.commands.createProject
import ru.quipy.logic.states.ProjectAggregateState
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectProjectionRepository: ProjectProjectionRepository,
    val projectionsService: ProjectionsService,
) {

    @PostMapping("/create")
    fun createProject(@RequestBody createProjectRequest: CreateProjectRequest): ProjectCreatedEvent {
        val participants = mutableListOf(ParticipantProjection(createProjectRequest.creatorId))

        val event = projectEsService.create {
            it.createProject(UUID.randomUUID(), createProjectRequest.title, createProjectRequest.creatorId, participants)
        }

        val projectProjection = ProjectProjection(
                projectId = event.projectId,
                title = event.title,
                creatorId = event.creatorId,
                participants = participants
        )
        projectProjectionRepository.save(projectProjection)

        return event
    }

    @PostMapping("/participants/create")
    fun addParticipant(@RequestBody request: AddParticipantRequest): ParticipantAddedEvent {
        val project = projectionsService.getProjectById(request.projectId)
                ?: throw NoSuchElementException("Project with ID ${request.projectId} does not exist")

        projectionsService.getUserById(UUID.fromString(request.userId))
                ?: throw NoSuchElementException("User with ID ${request.userId} does not exist")

        val event = projectEsService.update(request.projectId) {
            it.addParticipant(request.projectId, request.userId)
        }

        val updatedProject = project.apply {
            participants.add(ParticipantProjection(userId = request.userId))
        }

        projectProjectionRepository.save(updatedProject)

        return event
    }

    @GetMapping("/getUserProjects")
    fun getUserProjects(@RequestParam userId: String): List<ProjectProjection> {
        val projects = projectionsService.getProjectsByUserId(userId)
        if (projects.isEmpty()) {
            throw NoSuchElementException("No projects found for user with ID $userId")
        }
        return projects
    }

    @GetMapping("/{projectId}")
    fun getProjectById(@PathVariable projectId: UUID): ProjectProjection {
        return projectionsService.getProjectById(projectId)
                ?: throw NoSuchElementException("Project with ID $projectId does not exist")
    }

    @GetMapping("/all")
    fun getAllProjects(): List<ProjectProjection> {
        val projects = projectionsService.getAllProjects()
        if (projects.isEmpty()) {
            throw NoSuchElementException("No projects found")
        }
        return projects
    }
}

data class CreateProjectRequest(
        val title: String,
        val creatorId: String
)

data class AddParticipantRequest(
        val projectId: UUID,
        val userId: String,
)
