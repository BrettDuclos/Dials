package dials.filter.impl

import akka.actor.Props
import akka.testkit.TestActorRef
import dials.messages.StaticDataFilterApplicationMessage
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class TimeWindowFeatureFilterSpec extends AbstractFeatureFilterSpec {

    def "Validate static data requirements"() {
        setup:
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss")
        staticData.addDataObject(TimeWindowFeatureFilter.START_TIME, formatter.print(startTime))
        staticData.addDataObject(TimeWindowFeatureFilter.END_TIME, formatter.print(endTime))
        TestActorRef<TimeWindowFeatureFilter> actorRef = TestActorRef.create(system, Props.create(TimeWindowFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        startTime       | endTime         | expectedResult
        null            | null            | true
        new LocalTime() | null            | true
        null            | new LocalTime() | true
        ''              | new LocalTime() | true
        new LocalTime() | ''              | true
        new LocalTime() | new LocalTime() | false
    }

    def "Validate filter"() {
        setup:
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss")
        staticData.addDataObject(TimeWindowFeatureFilter.START_TIME, DateTimeFormat.forPattern("HH:mm:ss").print(startTime))
        staticData.addDataObject(TimeWindowFeatureFilter.END_TIME, DateTimeFormat.forPattern("HH:mm:ss").print(endTime))
        TestActorRef<TimeWindowFeatureFilter> actorRef = TestActorRef.create(system, Props.create(TimeWindowFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().filter() == expectedResult

        where:
        startTime                       | endTime                         | expectedResult
        LocalTime.MIDNIGHT              | new LocalTime().plusMinutes(1)  | true
        new LocalTime().minusMinutes(2) | new LocalTime().plusMinutes(1)  | true
        LocalTime.MIDNIGHT              | new LocalTime()                 | false
        new LocalTime().plusMinutes(1)  | new LocalTime().plusMinutes(2)  | false
        new LocalTime().minusMinutes(2) | new LocalTime().minusMinutes(1) | false
    }

    def "Validate dial pattern"() {
        setup:
        TestActorRef<TimeWindowFeatureFilter> actorRef = TestActorRef.create(system, Props.create(TimeWindowFeatureFilter.class))

        expect:
        actorRef.underlyingActor().consumeDialPattern(pattern) == expectedResult

        where:
        pattern        | expectedResult
        "1"            | null
        "1 hour"       | null
        "1 hour start" | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'start')
        "1 hour end"   | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'end')
        "2 hour start" | new TimeWindowFeatureFilter.TimeWindowPattern(2, 'hour', 'start')
        "2 hour end"   | new TimeWindowFeatureFilter.TimeWindowPattern(2, 'hour', 'end')
        "5 hour both"  | new TimeWindowFeatureFilter.TimeWindowPattern(5, 'hour', 'both')
    }

}