package dials.dial;

import dials.execution.ExecutionContext;

public interface Dialable {

    void dial(ExecutionContext executionContext);

    Object consumeDialPattern(String pattern);
}
