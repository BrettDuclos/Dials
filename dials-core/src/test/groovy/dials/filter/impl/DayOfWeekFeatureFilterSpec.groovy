package dials.filter.impl

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import dials.execution.ExecutionContext
import dials.filter.FilterData
import dials.messages.ContextualMessage
import dials.messages.StaticDataFilterApplicationMessage
import org.joda.time.DateTimeConstants
import org.joda.time.DateTime
import spock.lang.Specification

class DayOfWeekFeatureFilterSpec extends Specification {

    ActorSystem system;
    ContextualMessage message
    ExecutionContext context

    def setup() {
        system = ActorSystem.create()
        context = new ExecutionContext('fake')
        message = new ContextualMessage(context)
    }

    def "Validate static data requirements"() {
        setup:
        FilterData staticData = new FilterData();
        staticData.addDataObject(DayOfWeekFeatureFilter.DAYS_OF_WEEK, days)
        TestActorRef<DayOfWeekFeatureFilter> actorRef = TestActorRef.create(system, Props.create(DayOfWeekFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().abandoned == expectedResult

        where:
        days               | expectedResult
        'mon'              | false
        'tue'              | false
        'wed'              | false
        'thu'              | false
        'fri'              | false
        'sat'              | false
        'sun'              | false
        'monday'           | false
        'tuesday'          | false
        'wednesday'        | false
        'thursday'         | false
        'friday'           | false
        'saturday'         | false
        'sunday'           | false
        null               | true
        ''                 | true
        'fake'             | true
        'mon,tue,wed'      | false
        'thursday,fri,wed' | false
        'friday,sunday'    | false
    }

    def "Validate filter"() {
        setup:
        FilterData staticData = new FilterData();
        staticData.addDataObject(DayOfWeekFeatureFilter.DAYS_OF_WEEK, days)
        TestActorRef<DayOfWeekFeatureFilter> actorRef = TestActorRef.create(system, Props.create(DayOfWeekFeatureFilter.class))
        actorRef.underlyingActor().applyStaticData(new StaticDataFilterApplicationMessage(staticData, message))

        expect:
        actorRef.underlyingActor().filter() == expectedResult

        where:
        days                          | expectedResult
        'mon,tue,wed,thu,fri,sat,sun' | true
        getStringForToday()           | true
        getStringForTomorrow()        | false
    }

    private def getStringForToday() {
        int dayOfWeek = new DateTime().getDayOfWeek()
        return getStringForDay(dayOfWeek)
    }

    private def getStringForTomorrow() {
        int dayOfWeek = new DateTime().getDayOfWeek()
        if (dayOfWeek == DateTimeConstants.SUNDAY) {
            dayOfWeek = DateTimeConstants.MONDAY
        } else {
            dayOfWeek++
        }

        return getStringForDay(dayOfWeek)
    }

    private def getStringForDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case DateTimeConstants.MONDAY: return 'mon'
            case DateTimeConstants.TUESDAY: return 'tue'
            case DateTimeConstants.WEDNESDAY: return 'wed'
            case DateTimeConstants.THURSDAY: return 'thu'
            case DateTimeConstants.FRIDAY: return 'fri'
            case DateTimeConstants.SATURDAY: return 'sat'
            case DateTimeConstants.SUNDAY: return 'sun'
        }
    }

}


