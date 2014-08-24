package dials.filter.impl

import akka.actor.ActorSystem
import dials.DialsSystemConfiguration
import dials.datastore.DialsRepository
import dials.execution.ExecutionContext
import dials.filter.FilterData
import dials.messages.ContextualMessage
import dials.model.FeatureModel
import spock.lang.Specification

abstract class AbstractFeatureFilterSpec extends Specification {

    ActorSystem system;
    ContextualMessage message
    ExecutionContext context
    FilterData staticData
    FilterData dynamicData

    def mockConfiguration
    def mockRepository
    def stubFeature

    def setup() {
        mockConfiguration = Mock(DialsSystemConfiguration)
        mockRepository = Mock(DialsRepository)
        stubFeature = Stub(FeatureModel)
        stubFeature.featureName >> 'fake'

        system = ActorSystem.create()
        context = new ExecutionContext(stubFeature.featureName)
        message = new ContextualMessage(context, mockConfiguration)
        staticData = new FilterData()
        dynamicData = new FilterData()
    }

}