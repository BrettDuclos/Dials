package dials.filter.impl

import akka.actor.Props
import akka.testkit.TestActorRef
import dials.messages.StaticDataFilterApplicationMessage
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import spock.lang.Shared

class TimeWindowFeatureFilterSpec extends AbstractFeatureFilterSpec {

    @Shared
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss")

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
        staticData.addDataObject(TimeWindowFeatureFilter.START_TIME, formatter.print(startTime))
        staticData.addDataObject(TimeWindowFeatureFilter.END_TIME, formatter.print(endTime))
        TestActorRef<TimeWindowFeatureFilter> actorRef = TestActorRef.create(system, Props.create(TimeWindowFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().filter() == expectedResult

        where:
        startTime                       | endTime                          | expectedResult
        LocalTime.MIDNIGHT              | new LocalTime().plusMinutes(1)   | true
        new LocalTime().minusMinutes(2) | new LocalTime().plusMinutes(1)   | true
        new LocalTime().minusMinutes(5) | new LocalTime().minusMinutes(15) | true
        new LocalTime().minusMinutes(5) | new LocalTime().minusMinutes(5)  | true
        LocalTime.MIDNIGHT              | new LocalTime()                  | false
        new LocalTime().plusMinutes(1)  | new LocalTime().plusMinutes(2)   | false
        new LocalTime().minusMinutes(2) | new LocalTime().minusMinutes(1)  | false
        new LocalTime().plusMinutes(5)  | new LocalTime().minusMinutes(5)  | false
    }

    def "Validate dial pattern"() {
        setup:
        TestActorRef<TimeWindowFeatureFilter> actorRef = TestActorRef.create(system, Props.create(TimeWindowFeatureFilter.class))

        expect:
        actorRef.underlyingActor().consumeDialPattern(pattern) == expectedResult

        where:
        pattern         | expectedResult
        "1"             | null
        "1 hour"        | null
        "1 hour start"  | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'start')
        "1 hour end"    | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'end')
        "2 hour start"  | new TimeWindowFeatureFilter.TimeWindowPattern(2, 'hour', 'start')
        "2 hour end"    | new TimeWindowFeatureFilter.TimeWindowPattern(2, 'hour', 'end')
        "5 hour both"   | new TimeWindowFeatureFilter.TimeWindowPattern(5, 'hour', 'both')
        "-2 hour start" | new TimeWindowFeatureFilter.TimeWindowPattern(-2, 'hour', 'start')
        "-2 hour end"   | new TimeWindowFeatureFilter.TimeWindowPattern(-2, 'hour', 'end')
        "-5 hour both"  | new TimeWindowFeatureFilter.TimeWindowPattern(-5, 'hour', 'both')
    }

    def "Validate time calculations"() {
        setup:
        staticData.addDataObject(TimeWindowFeatureFilter.START_TIME, startTime)
        staticData.addDataObject(TimeWindowFeatureFilter.END_TIME, endTime)
        TestActorRef<TimeWindowFeatureFilter> actorRef = TestActorRef.create(system, Props.create(TimeWindowFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        when:
        actorRef.underlyingActor().calculateNewTimes(timeToAdd)

        then:
        actorRef.underlyingActor().startTime == newStartTime
        actorRef.underlyingActor().endTime == newEndTime
        actorRef.underlyingActor().crossesMidnight == crossesMidnight

        where:
        startTime | endTime | timeToAdd | newStartTime | newEndTime | crossesMidnight
        '10:00:00' | '12:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'start') | formatter.parseLocalTime('09:00:00') | formatter.parseLocalTime('12:00:00') | false
        '00:00:00' | '12:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'start') | formatter.parseLocalTime('23:00:00') | formatter.parseLocalTime('12:00:00') | true
        '00:00:00' | '12:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'end')   | formatter.parseLocalTime('00:00:00') | formatter.parseLocalTime('13:00:00') | false
        '04:00:00' | '23:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(2, 'hour', 'end')   | formatter.parseLocalTime('04:00:00') | formatter.parseLocalTime('01:00:00') | true
        '05:00:00' | '14:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(2, 'hour', 'both')  | formatter.parseLocalTime('03:00:00') | formatter.parseLocalTime('16:00:00') | false
        '00:00:00' | '23:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'both')  | formatter.parseLocalTime('00:00:00') | formatter.parseLocalTime('00:00:00') | true
        '23:00:00' | '01:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'start') | formatter.parseLocalTime('22:00:00') | formatter.parseLocalTime('01:00:00') | true
        '23:00:00' | '01:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(1, 'hour', 'end')   | formatter.parseLocalTime('23:00:00') | formatter.parseLocalTime('02:00:00') | true
        '04:00:00' | '23:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(-2, 'hour', 'end')  | formatter.parseLocalTime('04:00:00') | formatter.parseLocalTime('21:00:00') | false
        '05:00:00' | '14:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(-2, 'hour', 'both') | formatter.parseLocalTime('07:00:00') | formatter.parseLocalTime('12:00:00') | false
        '00:00:00' | '23:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(-1, 'hour', 'both')  | formatter.parseLocalTime('01:00:00') | formatter.parseLocalTime('22:00:00') | false
        '23:00:00' | '01:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(-1, 'hour', 'start') | formatter.parseLocalTime('00:00:00') | formatter.parseLocalTime('01:00:00') | false
        '23:00:00' | '01:00:00' | new TimeWindowFeatureFilter.TimeWindowPattern(-1, 'hour', 'end')   | formatter.parseLocalTime('23:00:00') | formatter.parseLocalTime('00:00:00') | true
    }
}