package dials.filter.impl

import akka.actor.Props
import akka.testkit.TestActorRef
import dials.filter.DynamicFilterDataConstants
import dials.messages.DynamicDataFilterApplicationMessage
import dials.messages.StaticDataFilterApplicationMessage

class SeededPercentageFeatureFilterSpec extends PercentageFeatureFilterSpec {

    def "Validate dynamic data requirements"() {
        setup:
        dynamicData.addDataObject(DynamicFilterDataConstants.SEED, seed)
        TestActorRef<SeededPercentageFeatureFilter> actorRef = TestActorRef.create(system, Props.create(SeededPercentageFeatureFilter.class))
        actorRef.underlyingActor().applyDynamicData(new DynamicDataFilterApplicationMessage(dynamicData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        seed       | expectedResult
        '5'        | false
        '100'      | false
        '25'       | false
        '5000'     | false
        'asdfewa'  | false
        new Date() | false
        null       | true
    }

    def "Validate filter"() {
        setup:
        staticData.addDataObject(PercentageFeatureFilter.PERCENTAGE, percentage)
        dynamicData.addDataObject(DynamicFilterDataConstants.SEED, seed)
        TestActorRef<SeededPercentageFeatureFilter> actorRef = TestActorRef.create(system,
                Props.create(SeededPercentageFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))
        actorRef.underlyingActor().applyDynamicData(new DynamicDataFilterApplicationMessage(dynamicData, message))

        expect:
        actorRef.underlyingActor().filter() == expectedResult

        where:
        percentage | seed           | expectedResult
        100        | ''             | true
        0          | ''             | false
        47         | 'test'         | false
        48         | 'test'         | true
        83         | new Integer(5) | false
        84         | new Integer(5) | true
    }
}