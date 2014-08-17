package dials.dial;

import dials.execution.ExecutionContext;
import dials.filter.FilterData;

public interface Dialable {

    void dial(FilterData data, ExecutionContext executionContext);
}
