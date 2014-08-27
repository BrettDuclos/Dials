package dials.filter.impl

import akka.actor.Props
import akka.testkit.TestActorRef
import dials.messages.StaticDataFilterApplicationMessage


abstract class PercentageFeatureFilterSpec extends AbstractFeatureFilterSpec {

    def "Validate static data requirements"() {
        setup:
        staticData.addDataObject(PercentageFeatureFilter.PERCENTAGE, percentage)
        TestActorRef<PercentageFeatureFilter> actorRef = TestActorRef.create(system, Props.create(PercentageFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        percentage | expectedResult
        '5'        | false
        '100'      | false
        '25'       | false
        '5000'     | false
        null       | true
        'a'        | true
    }

    def "Validate dial pattern"() {
        setup:
        TestActorRef<PercentageFeatureFilter> actorRef = TestActorRef.create(system, Props.create(PercentageFeatureFilter.class))

        expect:
        actorRef.underlyingActor().consumeDialPattern(pattern) == expectedResult

        where:
        pattern | expectedResult
        "1"     | 1
        "100"   | 100
        ""      | null
        null    | null
    }

    def "Validate Percentage Adjustment"() {
        setup:
        staticData.addDataObject(PercentageFeatureFilter.PERCENTAGE, percentage)
        TestActorRef<PercentageFeatureFilter> actorRef = TestActorRef.create(system, Props.create(PercentageFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))
        actorRef.underlyingActor().adjustPercentage(adjustment)

        expect:
        actorRef.underlyingActor().getPercentage() == expectedResult

        where:
        percentage | adjustment | expectedResult
        50         | 5          | 55
        50         | -5         | 45
        50         | 51         | 100
        50         | -51        | 0
        0          | -1         | 0
        100        | 1          | 100
    }

}