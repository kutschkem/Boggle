package boggle.game;

/**
 * The status that a word can have in a Boggle Game. These are closely connected with the
 * scoring, since only ACCEPTED words get points.
 * @author Michael
 *
 */
public enum WordStatus {

	ACCEPTED,
	DOUBLE,
	/**
	 * The word cannot be formed using the current field
	 */
	IMPOSSIBLE_WORD,
	ON_BLACKLIST,
	UNKNOWN;
}
