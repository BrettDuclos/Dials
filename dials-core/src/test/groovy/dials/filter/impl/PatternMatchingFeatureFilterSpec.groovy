package dials.filter.impl

import akka.actor.Props
import akka.testkit.TestActorRef
import dials.filter.DynamicFilterDataConstants
import dials.messages.DynamicDataFilterApplicationMessage
import dials.messages.StaticDataFilterApplicationMessage

class PatternMatchingFeatureFilterSpec extends AbstractFeatureFilterSpec {

    def "Validate static data requirements"() {
        setup:
        staticData.addDataObject(PatternMatchingFeatureFilter.PATTERN, pattern)
        TestActorRef<PatternMatchingFeatureFilter> actorRef = TestActorRef.create(system, Props.create(PatternMatchingFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        pattern | expectedResult
        'test'  | false
        ''      | false
        'a*b'   | false
        5       | true
        null    | true
    }

    def "Validate dynamic data requirements"() {
        setup:
        dynamicData.addDataObject(DynamicFilterDataConstants.MATCH_STRING, matchString)
        TestActorRef<PatternMatchingFeatureFilter> actorRef = TestActorRef.create(system, Props.create(PatternMatchingFeatureFilter.class))
        actorRef.underlyingActor().applyDynamicData(new DynamicDataFilterApplicationMessage(dynamicData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        matchString | expectedResult
        'test'      | false
        ''          | false
        5           | true
        null        | true
    }

    def "Validate filter"() {
        setup:
        staticData.addDataObject(PatternMatchingFeatureFilter.PATTERN, pattern)
        dynamicData.addDataObject(DynamicFilterDataConstants.MATCH_STRING, matchString)
        TestActorRef<PatternMatchingFeatureFilter> actorRef = TestActorRef.create(system, Props.create(PatternMatchingFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))
        actorRef.underlyingActor().applyDynamicData(new DynamicDataFilterApplicationMessage(dynamicData, message))

        expect:
        actorRef.underlyingActor().filter() == expectedResult

        where:
        pattern        | matchString | expectedResult
        ""             | ""          | true
        "a"            | "a"         | true
        "a"            | "b"         | false
        "b"            | "a"         | false
        "a*b"          | "aaaaab"    | true
        "(hello|moto)" | "hello"     | true
        "(hello|moto)" | "moto"      | true
        "(hello|moto)" | "party"     | false
    }
}