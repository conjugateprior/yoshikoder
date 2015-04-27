package edu.harvard.wcfia.yoshikoder.reporting;

/**
 * For when risk ratios cannot be computed.  This is promarily
 * to flag cases where
 * where a zero count would trigger a divide by zero
 * error.
 * 
 * @author will
 *
 */
public class UncomputableRiskRatioException extends Exception {

    public UncomputableRiskRatioException() {
        super();

    }

    public UncomputableRiskRatioException(String message) {
        super(message);

    }

    public UncomputableRiskRatioException(String message, Throwable cause) {
        super(message, cause);

    }

    public UncomputableRiskRatioException(Throwable cause) {
        super(cause);

    }

}
