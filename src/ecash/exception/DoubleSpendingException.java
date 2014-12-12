package ecash.exception;

public class DoubleSpendingException extends Exception {
    public final int gu;
    public final int U;
    public DoubleSpendingException (int gu, int U){
            this.gu=gu;this.U=U;
    }
}
