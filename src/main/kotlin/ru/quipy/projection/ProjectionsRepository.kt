package ru.quipy.projection

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface ProjectProjectionRepository : MongoRepository<ProjectProjection, UUID> {
    fun findByParticipantsUserIdContaining(userId: String): List<ProjectProjection>
}

interface TaskProjectionRepository : MongoRepository<TaskProjection, UUID>

interface StatusProjectionRepository : MongoRepository<StatusProjection, UUID>

interface UserProjectionRepository : MongoRepository<UserProjection, UUID> {
    fun findByEmail(email: String): UserProjection?
}
