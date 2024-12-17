package ru.quipy.logic.commands

import ru.quipy.api.events.UserCreatedEvent
import ru.quipy.logic.states.UserAggregateState
import java.util.*

fun UserAggregateState.createUser(id: UUID, username: String, login: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            username = username,
            login = login,
            password = password
    )
}
