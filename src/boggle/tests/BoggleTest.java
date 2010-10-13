package boggle.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import boggle.game.BoggleGame;
import boggle.game.BoggleRules;
import boggle.game.Dictionary;

public class BoggleTest {

	
	@Test
	public void testRandomness(){
		BoggleRules rules = new BoggleRules();
		assertEquals('a', rules.getChar(0.0));
		assertEquals('b', rules.getChar(10.0 / 96.0));
		assertEquals('z', rules.getChar(1.0));
	}
	
	@Test
	public void testRestart(){
		BoggleGame g = new BoggleGame();
		g.restart();
		for(int i = 0; i < g.getField().length; i++){
			for(int j = 0; j < g.getField()[i].length; j++){
				System.out.print(g.getField()[i][j]);
			}
			System.out.print('\n');
		}
	}
	
	@Test
	public void dictionaryTest(){
		Dictionary dic = new Dictionary();
		dic.addWord("Hummel");
		assertEquals(true, dic.check("Hummel"));
		assertEquals(true, dic.check("Abdikation"));
		assertEquals(true, dic.check("abdikation"));
		assertEquals(false, dic.check("grradsdds"));
		assertEquals(true,dic.check("�pfel"));
		assertEquals(true, dic.check("Aepfel"));
		assertEquals(true, dic.check("�se"));		
	}
	
	@Test
	public void test_dictionary_Save_Load(){
		Dictionary dic = new Dictionary();
		File file = new File("dic.txt");
		String word = "grradsdds";
		if(file.exists())
			file.delete();
		dic.addWord(word);
		assertEquals(true, dic.check(word));
		try{
		dic.save("dic.txt");
		dic.reset();
		dic.load("dic.txt");
		}catch(IOException ex){
			assert(false);
		}
		assertEquals(true, dic.check(word));
		file.delete();
		
	}
	
	@Test
	public void Dictionary_removeTest(){
		Dictionary dic = new Dictionary(false);
		String word = "grardarasa";
		dic.addWord(word);
		assertEquals(true, dic.check(word));
		dic.removeWord(word);
		assertEquals(false, dic.check(word));
	}
	
	@Test
	public void evaluateTest(){
		BoggleGame g = new BoggleGame();
		assertEquals(4,g.evaluate(Arrays.asList("mai","tal","oh","fuenf")));
		assertEquals(1,g.evaluate(Arrays.asList("alt")));
		assertEquals(3,g.evaluate(Arrays.asList("kronen")));
	}
}
