package dials.filter;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.Map;

public class FilterDataHelper {

    private Map<String, Object> dataObjects;
    private String[] dateTimePatterns = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};
    private String[] localTimePatterns = new String[]{"HH:mm:ss", "HH:mm", "HH"};

    public FilterDataHelper(FilterData filterData) {
        dataObjects = filterData.getDataObjects();
    }

    public <T> T getData(String dataName, Class<T> expectedType) throws FilterDataException {
        Object dataObject = dataObjects.get(dataName.toLowerCase());

        if (isNull(dataObject)) {
            throw new FilterDataException("Data " + dataName + " was not provided");
        }

        if (isAssignableFrom(dataObject, expectedType)) {
            return expectedType.cast(dataObject);
        } else {
            try {
                return attemptStringBasedDataConverstion(dataName, String.class.cast(dataObject), expectedType);
            } catch (ClassCastException e) {
                throw getDataRepresentativeFilterDataException(dataName, expectedType);
            }
        }
    }

    private boolean isNull(Object object) {
        return object == null;
    }

    private boolean isAssignableFrom(Object object, Class<?> expectedType) {
        return isAssignableFrom(object.getClass(), expectedType);
    }

    private boolean isAssignableFrom(Class<?> providedType, Class<?> expectedType) {
        return expectedType.isAssignableFrom(providedType);
    }

    private <T> T attemptStringBasedDataConverstion(String dataName, String dataObject, Class<T> expectedType) throws FilterDataException {
        if (isAssignableFrom(String.class, expectedType)) {
            return handleString(dataObject, expectedType);
        } else if (isAssignableFrom(Integer.class, expectedType)) {
            return handleInteger(dataName, dataObject, expectedType);
        } else if (isAssignableFrom(Long.class, expectedType)) {
            return handleLong(dataName, dataObject, expectedType);
        } else if (isAssignableFrom(BigDecimal.class, expectedType)) {
            return handleBigDecimal(dataName, dataObject, expectedType);
        } else if (isAssignableFrom(DateTime.class, expectedType)) {
            return handleDateTime(dataName, dataObject, expectedType);
        } else if (isAssignableFrom(LocalTime.class, expectedType)) {
            return handleLocalTime(dataName, dataObject, expectedType);
        } else {
            throw new FilterDataException("Provided type " + expectedType.getSimpleName() + " is not yet supported");
        }
    }

    private <T> T handleString(String dataObject, Class<T> expectedType) {
        return expectedType.cast(dataObject);
    }

    private <T> T handleInteger(String dataName, String dataObject, Class<T> expectedType) throws FilterDataException {
        try {
            return expectedType.cast(Integer.parseInt(dataObject));
        } catch (NumberFormatException | ClassCastException e) {
            throw getDataRepresentativeFilterDataException(dataName, expectedType);
        }
    }

    private <T> T handleDateTime(String dataName, String dataObject, Class<T> expectedType) throws FilterDataException {
        for (String pattern : dateTimePatterns) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);

            try {
                return expectedType.cast(formatter.parseDateTime(dataObject));
            } catch (Exception e) {
                continue;
            }
        }

        throw getDataRepresentativeFilterDataException(dataName, expectedType);
    }

    private <T> T handleLocalTime(String dataName, String dataObject, Class<T> expectedType) throws FilterDataException {
        for (String pattern : localTimePatterns) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);

            try {
                return expectedType.cast(formatter.parseLocalTime(dataObject));
            } catch (Exception e) {
                continue;
            }
        }

        throw getDataRepresentativeFilterDataException(dataName, expectedType);
    }

    private <T> T handleLong(String dataName, String dataObject, Class<T> expectedType) throws FilterDataException {
        try {
            return expectedType.cast(Long.parseLong(dataObject));
        } catch (NumberFormatException | ClassCastException e) {
            throw getDataRepresentativeFilterDataException(dataName, expectedType);
        }
    }

    private <T> T handleBigDecimal(String dataName, String dataObject, Class<T> expectedType) throws FilterDataException {
        try {
            return expectedType.cast(new BigDecimal(dataObject));
        } catch (NumberFormatException | ClassCastException e) {
            throw getDataRepresentativeFilterDataException(dataName, expectedType);
        }
    }

    private FilterDataException getDataRepresentativeFilterDataException(String dataName, Class expectedType) {
        return new FilterDataException("Unable to represent " + dataName + " as " + expectedType.getSimpleName());
    }
}

