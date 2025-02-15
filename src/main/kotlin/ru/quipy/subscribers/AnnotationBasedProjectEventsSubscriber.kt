package ru.quipy.subscribers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.events.ProjectCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "demo-subs-stream"
)
class AnnotationBasedProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: ProjectCreatedEvent) {
        logger.info("Task created: {}", event.title)
    }

}