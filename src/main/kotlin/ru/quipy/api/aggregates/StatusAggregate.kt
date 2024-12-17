package ru.quipy.api.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "aggregate-status")
class StatusAggregate : Aggregate
