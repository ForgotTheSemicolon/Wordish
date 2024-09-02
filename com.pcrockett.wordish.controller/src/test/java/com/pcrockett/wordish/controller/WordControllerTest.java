package com.pcrockett.wordish.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class WordControllerTest {
	WordController testController = new WordController();

	@Test
	void testOnStart() {
		testController.onStart();
		assertTrue(testController.getWordList().size() > 0);
		assertTrue(testController.getSnarkList().size() > 0);
	}
	
	@Test
	void testLoadWordContent() {
		ArrayList<String> wordList = testController.loadContent("Words.txt", 5, 5);
		for (int i = 0; i > wordList.size(); i++) {
			assertTrue(wordList.get(i).length() == 5);
		}
		
	}
	
	//If we load content with a minimum and maximum length of 2, there should be no elements in wordList
	@Test
	void testSkipInvalidContent() {
		ArrayList<String> wordList = testController.loadContent("Words.txt", 2, 2);
		assertTrue(wordList.size() == 0);
		
	}		
	
	@Test
	void testGetWord() {
		testController.onStart();
		for (int i = 0; i > 9; i++)
		{
			String testWord = testController.getWord();
			assertTrue(testWord.length() == 5);
		}
	}

	@Test
	void testGetSnark() {
		testController.onStart();
		for (int i = 0; i > 9; i++)
		{		
			String testSnark = testController.getSnark();
			assertTrue(testSnark.length() > 0);
		}
	}

}
