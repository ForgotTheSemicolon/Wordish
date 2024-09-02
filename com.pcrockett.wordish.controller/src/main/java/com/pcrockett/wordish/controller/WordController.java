//Project built using Spring Boot Initializr
package com.pcrockett.wordish.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for Wordish application.
 * Provides REST endpoints to return random words from files.
 * Currently words are provided as plain text for simplicity and flexibility.
 * Any word-guessing logic is currently handled client side, which allows the possibility of
 * multiple different word games using the same service.
 */
@RestController
public class WordController {

	//Array to store the words
	private ArrayList<String> wordList;
	//List of snarky sayings. Just for fun.
	private ArrayList<String> snarkList;
	//Shared Random object - no need to have a different one for each endpoint.
	private Random randomGenerator = new Random();
	
	//This could be updated to put the word lists in their own list, possibly with the associated filename stored too.
	//Getter for wordList
	public ArrayList<String> getWordList() {
		return wordList;
	}
	
	//Getter for snarkList
	public ArrayList<String> getSnarkList() {
		return snarkList;
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void onStart() {
		wordList = loadContent("Words.txt", 5, 5);
		snarkList = loadContent("Snark.txt", 1, 35);
	}	
	
	//REST endpoint for random word
	@GetMapping("/getWord")
	public String getWord() {
		return getRandomWord(wordList, randomGenerator);
	}
	
	//REST endpoint for random snark
	@GetMapping("/getSnark")
	public String getSnark() {
		return getRandomWord(snarkList, randomGenerator);
	}	
	
	//Read and return a random word from the ArrayLIst
	private String getRandomWord(ArrayList<String> wList, Random rGen) {
		int index = rGen.nextInt(wList.size());
		String randomWord = wList.get(index);
		return randomWord;
	}
	
	//Load words from file into an array.
	//This is faster than reading from the file each time getWord is called.
	//Performs simple validation by checking length of each line and skips invalid lines
	public ArrayList<String> loadContent(String fName, int minLength, int maxLength) {
		Scanner s;
		ArrayList<String> list = new ArrayList<String>();
		try {
			s = new Scanner(new File(fName));  
			
			//Each line goes in an ArrayList element.
			while (s.hasNextLine()){
				String nextWord = s.nextLine();
				if (nextWord.length() >= minLength && nextWord.length() <= maxLength)
				{
					list.add(nextWord);
				}
				else
				{
					System.out.println("Invalid length on item: " + nextWord);
				}
			}
			s.close();	
		} catch (FileNotFoundException e) {
			list.clear();		
			String errString = "Content file not found!";
			//Responses that begin with * are error messages			
			list.add("*"+errString); 
			System.out.println(errString);
		}		
		return list;
	}

}
