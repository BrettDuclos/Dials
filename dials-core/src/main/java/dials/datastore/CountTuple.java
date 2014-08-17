package dials.datastore;

import java.math.BigDecimal;

public class CountTuple {

    private final int DECIMAL_ADJUSTMENT = 100;
    private int executions;
    private BigDecimal rateOfSuccess;

    public CountTuple(int executions, int errors) {
        this.executions = executions;

        rateOfSuccess = new BigDecimal(((executions - errors) / (executions * 1.0)) * DECIMAL_ADJUSTMENT);
    }

    public int getExecutions() {
        return executions;
    }

    public BigDecimal getRateOfSuccess() {
        return rateOfSuccess;
    }
}
