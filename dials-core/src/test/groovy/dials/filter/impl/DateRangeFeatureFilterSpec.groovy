package dials.filter.impl

import akka.actor.Props
import akka.testkit.TestActorRef
import dials.messages.StaticDataFilterApplicationMessage
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class DateRangeFeatureFilterSpec extends AbstractFeatureFilterSpec {

    def "Validate static data requirements"() {
        setup:
        staticData.addDataObject(DateRangeFeatureFilter.START_DATE, startDate)
        staticData.addDataObject(DateRangeFeatureFilter.END_DATE, endDate)
        TestActorRef<DateRangeFeatureFilter> actorRef = TestActorRef.create(system, Props.create(DateRangeFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        startDate             | endDate               | expectedResult
        null                  | null                  | true
        ''                    | ''                    | true
        '2000-01-01'          | null                  | false
        '2000-01-01 00:00'    | null                  | false
        '2000-01-01 00:00:00' | null                  | false
        '20000101'            | null                  | true
        null                  | '2000-01-01'          | true
        null                  | '2000-01-01 00:00'    | true
        null                  | '2000-01-01 00:00:00' | true
        '2000-01-01'          | '2000-01-01'          | false
        '2000-01-01 00:00'    | '2000-01-01 00:00'    | false
        '2000-01-01 00:00:00' | '2000-01-01 00:00'    | false
    }

    def "Validate filter"() {
        setup:
        DateTimeFormatter formatter = DateTimeFormat.forPattern('yyyy-MM-dd');
        staticData.addDataObject(DateRangeFeatureFilter.START_DATE, formatter.print(startDate))
        staticData.addDataObject(DateRangeFeatureFilter.END_DATE, formatter.print(endDate))
        TestActorRef<DateRangeFeatureFilter> actorRef = TestActorRef.create(system, Props.create(DateRangeFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().filter() == expectedResult

        where:
        startDate                   | endDate                     | expectedResult
        new DateTime()              | new DateTime().plusDays(1)  | true
        new DateTime().minusDays(5) | new DateTime().plusDays(1)  | true
        new DateTime().minusDays(5) | new DateTime().minusDays(1) | false
        new DateTime().plusDays(5)  | new DateTime().plusDays(10) | false
    }

    def "Validate dial pattern"() {
        setup:
        TestActorRef<DateRangeFeatureFilter> actorRef = TestActorRef.create(system, Props.create(DateRangeFeatureFilter.class))

        expect:
        actorRef.underlyingActor().consumeDialPattern(pattern) == expectedResult

        where:
        pattern | expectedResult
        "1"     | 1
        "100"   | 100
        ""      | null
        null    | null
    }
}