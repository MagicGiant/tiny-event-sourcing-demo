package ru.quipy.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.api.aggregates.StatusAggregate
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.states.ProjectAggregateState
import ru.quipy.logic.states.TaskAggregateState
import ru.quipy.logic.states.StatusAggregateState
import ru.quipy.logic.states.UserAggregateState
import ru.quipy.subscribers.AnnotationBasedProjectEventsSubscriber
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class EventSourcingLibConfiguration {


    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var projectEventSubscriber: AnnotationBasedProjectEventsSubscriber

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun taskEsService() = eventSourcingServiceFactory.create<UUID, TaskAggregate, TaskAggregateState>()

    @Bean
    fun statusEsService() = eventSourcingServiceFactory.create<UUID, StatusAggregate, StatusAggregateState>()

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<ProjectAggregate>(projectEventSubscriber)
    }
}
