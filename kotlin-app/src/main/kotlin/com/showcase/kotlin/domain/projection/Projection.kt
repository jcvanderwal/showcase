package com.showcase.kotlin.domain.projection

import com.showcase.kotlin.domain.event.DomainEvent

interface Projection {
    suspend fun handle(event: DomainEvent)
}
