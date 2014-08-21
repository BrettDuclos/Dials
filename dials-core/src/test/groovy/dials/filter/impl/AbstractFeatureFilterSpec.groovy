package dials.filter.impl

import akka.actor.ActorSystem
import dials.DialsSystemConfiguration
import dials.datastore.DataStore
import dials.execution.ExecutionContext
import dials.filter.FilterData
import dials.messages.ContextualMessage
import spock.lang.Specification

abstract class AbstractFeatureFilterSpec extends Specification {

    ActorSystem system;
    ContextualMessage message
    ExecutionContext context
    FilterData staticData
    FilterData dynamicData

    def mockConfiguration
    def mockDataStore

    def setup() {
        mockConfiguration = Mock(DialsSystemConfiguration)
        mockDataStore = Mock(DataStore)

        system = ActorSystem.create()
        context = new ExecutionContext('fake')
        message = new ContextualMessage(context, mockConfiguration)
        staticData = new FilterData()
        dynamicData = new FilterData()
    }

}