package dials.filter

import org.joda.time.DateTime
import spock.lang.Specification

class FilterDataHelperSpec extends Specification {

    String fakeName = "fake"
    FilterData data

    def setup() {
        data = new FilterData()
    }

    def "Exceptional scenarios"() {
        setup:
        def helper = setupDataHelper(input)

        when:
        helper.getData(fakeName, expectedClass)

        then:
        thrown(FilterDataException)

        where:
        input  | expectedClass
        null   | String.class
        "Test" | FilterDataHelper.class

    }

    def "Valid scenarios"() {
        setup:
        def helper = setupDataHelper(input)

        expect:
        helper.getData(fakeName, expectedClass) == result

        where:
        input                 | expectedClass    | result
        "Test"                | String.class     | "Test"
        1                     | Integer.class    | 1
        1L                    | Long.class       | 1L
        "1"                   | BigDecimal.class | new BigDecimal("1")
        "2000-01-01"          | DateTime.class   | new DateTime(2000, 1, 1, 0, 0)
        "2000-01-01 00:00"    | DateTime.class   | new DateTime(2000, 1, 1, 0, 0)
        "2000-01-01 00:00:00" | DateTime.class   | new DateTime(2000, 1, 1, 0, 0)
    }

    private FilterDataHelper setupDataHelper(Object object) {
        data.addDataObject(fakeName, object)
        return new FilterDataHelper(data)
    }
}
