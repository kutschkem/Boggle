package boggle.exceptions;


public class BoggleTimexception extends BoggleRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6874944995540245209L;

	public BoggleTimexception() {
		super("Time limit exceeded");
	}

	public BoggleTimexception(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public BoggleTimexception(Throwable arg0) {
		super("Time limit exceeded", arg0);
		// TODO Auto-generated constructor stub
	}

	public BoggleTimexception(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
