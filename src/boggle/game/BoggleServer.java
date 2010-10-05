package boggle.game;

import boggle.exceptions.GameIsRunningException;

public interface BoggleServer {

	public void registerClient(BoggleClient cl);
	public void unregisterClient(BoggleClient cl);
	
	public void setRules(BoggleRules rules) throws GameIsRunningException;
	//public void getRules(BoggleRules rules);
	
	public void setDictionary(Dictionary dic) throws GameIsRunningException;
	//public void getDictionary(Dictionary dic);
	
	//public char[][] getField();
}
