package test;

import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpClient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pcrockett.wordish.client.WordishGui;

class WordishGuiTest {
	
	//We have to initialize the GUI elements or the tests won't work
	@BeforeAll
	static void initialize() {
		WordishGui.initialize();
	}
	
	//StartGame should return "START" if successful.
	@Test
	void testStartGame() {
		assertEquals("START", WordishGui.startGame());
	}

	//RESTResponse for getWord should return a 5-letter word.
	@Test
	void testGetRESTResponse() {
		String testWord = "";
		boolean exceptionThrown = false;
		try {
			testWord = WordishGui.getRESTResponse("http://localhost:8080/getWord", HttpClient.newHttpClient());
		}
		catch (Exception e)
		{
			exceptionThrown = true;
		}
		assertEquals(exceptionThrown, false);
		assertEquals(testWord.length(), 5);
	}

	//Test a guess that matches the secret word
	//Success = every element of the return value is 2 (green letter)
	@Test
	void testCorrectTestGuess() {		
		String testWord = "HIRED";
		WordishGui.setWordToGuess(testWord);
		int[] testResults = WordishGui.testGuess(testWord);
		for (int i = 0; i < testResults.length; i++)
		{
			System.out.println("testResults["+i+"] == " + testResults[i]);			
		}		
		int[] correctResults = {2, 2, 2, 2, 2};
		assertArrayEquals(testResults, correctResults);
	}
	
	//Test a guess that does not match the secret word
	//Success = NOT every element of the return value is 2 (green letter)
	@Test
	void testIncorrectTestGuess() {
		WordishGui.setWordToGuess("AAAAA");
		int[] testResults = WordishGui.testGuess("ZZZZZ");
		//If somehow the results length is not 5, that's a failure.
		assertEquals(testResults.length, 5);
		int numCorrectLetters = 0;
		for (int i = 0; i < testResults.length; i++)
		{
			if(testResults[i] == 2) numCorrectLetters++;			
		}
		assertNotEquals(numCorrectLetters, 5);
	}	

	//Test isValidInput with a 5-letter word
	@Test
	void testValidInput() {
		assertEquals(WordishGui.isValidInput("OFFER"), true);
	}

	//Test isValidInput with a word that's too short
	@Test
	void testTooShortInput() {
		assertEquals(WordishGui.isValidInput("BAD"), false);
	}	

	//Test isValidInput with a word that's too long
	@Test
	void testTooLongInput() {
		assertEquals(WordishGui.isValidInput("THISISTOOLONG"), false);
	}	
	
	//Test isValidInput with a non-word
	@Test
	void testNotAWordInput() {
		assertEquals(WordishGui.isValidInput("R2-D2"), false);
	}	

}
