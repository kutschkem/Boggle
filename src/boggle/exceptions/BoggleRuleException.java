package boggle.exceptions;

/**
 * Indicates a Violation of the Game Rules.
 * @author Michael
 *
 */
@SuppressWarnings("serial")
public class BoggleRuleException extends Exception {

	public BoggleRuleException() {
	}

	public BoggleRuleException(String arg0) {
		super(arg0);
	}

	public BoggleRuleException(Throwable arg0) {
		super(arg0);
	}

	public BoggleRuleException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
