package com.pcrockett.wordish.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.JButton;

/**
 * Web-based Wordle clone. Relies on webservice to choose a random word.
 */
public class WordishGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JPanel panel;
    private static JFrame frame;

    private static JLabel Title;
    private static JLabel stats;
    private static JTextField guessText;
    private static JButton enterButton;
    private static JLabel[] labels;
    private static JLabel messages;
    private static JLabel snarkyComment;

    static int tries;
    static char[] input;
    static char[] answer;
    static boolean done;
    static String wordToGuess;
	
	
	public static void main(String[] args) {
	
			initialize();

	        String startResponse = startGame();
	        //If the game couldn't start, disable all interactive fields to avoid GUI weirdness.
	        if (startResponse != "START") {
	        	messages.setText(startResponse);
	        	messages.setVisible(true);
	        	enterButton.setEnabled(false);
	        	guessText.setEnabled(false);	        	
	        }
	    }

		//Return "START" if able to connect to webservice and start, error otherwise
	    public static String startGame() {
	        tries = 0;
	        answer = new char[5];
	        try {
	        	HttpClient client = HttpClient.newHttpClient();
	        	
	        	//Get random word from webservice
	        	setWordToGuess(getRESTResponse("http://localhost:8080/getWord", client));
	        	System.out.println("If you want to cheat, the word is " + wordToGuess);
	        	
	        	//If word begins with *, it's an error message
	        	if (wordToGuess.startsWith("*")) {
	        		return wordToGuess.substring(1);
	        	}
	        	
	        	
	        	//Get random snarky comment from webservice
	        	snarkyComment.setText(getRESTResponse("http://localhost:8080/getSnark", client));
	        		        	
	        	
		        for (int i = 0; i < 5; i++ ) answer[i] = wordToGuess.charAt(i);
	        }
	        catch (Exception e)
	        {
	        	System.out.println("Exception: " + e.getMessage());
	        	return "Unable to connect to webservice!";
	        }	              

	        input = new char[5];
	        return "START";
	    }	   
	    
	    //Set everything up. I added this method to make JUnit tests easier
	    public static void initialize() {
		        panel = new JPanel();
		        frame = new JFrame();
		        frame.setSize(250, 380);
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        frame.setTitle("Wordish");
		        frame.setLocationRelativeTo(null);
		        frame.add(panel);

		        panel.setLayout(null);
		        Title = new JLabel("Wordish!");
		        Title.setBounds(10, 10, 80, 25);
		        Title.setFont(new Font("Sans Serif", Font.PLAIN, 20));
		        panel.add(Title);	        

		        panel.setLayout(null);
		        	        
		        snarkyComment = new JLabel("Loading snarky comment...");
		        snarkyComment.setBounds(10, 40, 200, 25);
		        snarkyComment.setFont(new Font("Sans Serif", Font.PLAIN, 10));
		        snarkyComment.setForeground(Color.GRAY);
		        panel.add(snarkyComment);
		        
		        stats = new JLabel("Type a five letter word:");
		        stats.setBounds(10, 120, 185, 25);
		        panel.add(stats);

		        DocumentFilter dFilter = new UpcaseFilter();
		        guessText = new JTextField();
		        ((AbstractDocument) guessText.getDocument()).setDocumentFilter(dFilter);
		        
		        guessText.addActionListener(new WordishGui());
		        guessText.setBounds(40, 160 + (0 * 25), 80, 25);
		        panel.add(guessText);

		        enterButton = new JButton("Enter");
		        enterButton.setBounds(40, 90, 80, 25);
		        enterButton.addActionListener(new WordishGui());
		        panel.add(enterButton);

		        labels = new JLabel[6];
		        for (int i = 0; i < 6; i++) {
		            labels[i] = new JLabel("<html><font size='5' color=blue> ----- </font> <font");
		            labels[i].setBounds(44, 160 + (i * 25), 90, 25);
		            panel.add(labels[i]);
		        }
		        
		        //Message for invalid word length or other errors
		        messages = new JLabel("");
		        messages.setBounds(10, 310, 200, 25);	
		        messages.setForeground(Color.RED);
		        panel.add(messages);	        
		        messages.setVisible(false);

		        frame.setVisible(true);	    	
	    }
	    
	    //Set the word to guess
	    public static void setWordToGuess(String guessWord) {
	    	wordToGuess = guessWord;
	    }
	    
	    //Get REST response from specified URL with provided client
	    public static String getRESTResponse(String url, HttpClient client) throws Exception {
	    	try {
	    		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).build();
        		HttpResponse<String> httpResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        		return httpResponse.body();
	    	}
	    	catch (Exception e) {
	    		throw e;
	    	}
	    }
	    
	    //End the game and set text based on whether the word to guess was found or not.
	    public static void endGame() {

	        guessText.setEnabled(false);
	        guessText.setVisible(false);
	        enterButton.setEnabled(false);

	        if (!done) {
	        	stats.setText("The answer was: " + new String(wordToGuess));
	        	stats.setForeground(Color.RED);
	        	stats.setFont(new Font("Sans Serif", Font.PLAIN, 10));
	        }
	        else {
	        	if (tries == 1)
	        	{
		        	stats.setText("Lucky guess!");
	        	}
	        	else {
		        	stats.setText("You found the answer in " + tries + " tries!");
	        	}
	        	stats.setForeground(Color.BLUE);
	        	stats.setFont(new Font("Sans Serif", Font.PLAIN, 10));
	        }
	    }

	    //Triggered when the player clicks the Enter or presses Enter on the keyboard
	    @Override
	    public void actionPerformed(ActionEvent e) {
			enterWord();
	    }

	    //Check word length and submit guess
	    private static void enterWord(){
	        if (isValidInput(guessText.getText()) ) {
	        	messages.setVisible(false);
	        	processInput();
	        }
	        else {
	        	messages.setText("Invalid input!");
	        	messages.setVisible(true);
	        }
	    }

	    
	    //Process the guess and update GUI
	    public static void processInput(){
	        guessText.setBounds(40, 160 + ((tries + 1) * 25), 80, 25);

	        String userInput = guessText.getText().toUpperCase();
	        int[] colorOfLetters = testGuess(userInput);

	        done = true;
	        for (int i : colorOfLetters) {
	            if (i != 2) done = false;
	        }
	        if (done || tries > 5) endGame();

	        String[] numsToColors = new String[5];
	        for (int i = 0; i < 5; i++) {
	            if (colorOfLetters[i] == 0) numsToColors[i] = "black";
	            else if (colorOfLetters[i] == 1) numsToColors[i] = "orange";
	            else if (colorOfLetters[i] == 2) numsToColors[i] = "green";
	        }

	        //HTML is used here so each letter can be a different color.
	        //I thought about using HTML for all screen text, but decided that made the code hard to read.
	        String finalString = (
	        "<html><font size='5' color=" + numsToColors[0] + "> " + userInput.charAt(0) + "</font> <font            " + 
	        "<html><font size='5' color=" + numsToColors[1] + "> " + userInput.charAt(1) + "</font> <font            " + 
	        "<html><font size='5' color=" + numsToColors[2] + "> " + userInput.charAt(2) + "</font> <font            " + 
	        "<html><font size='5' color=" + numsToColors[3] + "> " + userInput.charAt(3) + "</font> <font            " + 
	        "<html><font size='5' color=" + numsToColors[4] + "> " + userInput.charAt(4) + "</font> <font            ");
	        setNextLabel(finalString);

	        //Clear any previous error messages
	        guessText.setText(""); 
	    }

	    public static int[] testGuess(String inputWord) {
	        done = false;
	        tries++;

	        //Should already be uppercase, but just in case...
	        String R1 = inputWord.toUpperCase();
	        
	        //Put guess into an array of chars
            for (int i = 0; i < 5; i++ ) {
                input[i] = R1.charAt(i);
            }
	        
	        //Put answer into array of chars
	        for (int i = 0; i < 5; i++ ) answer[i] = wordToGuess.charAt(i);
	        return getLetterColors(input, answer);
	    }

	    //Add the colored text for a guess to the GUI
	    public static void setNextLabel(String string){
	        labels[tries - 1].setText(string);
	    }

	    //Get the color for each letter of a guess based on Wordle rules
	    public static int[] getLetterColors(char[] inputWord, char[] correctWord) {	    	
	        char[] answerTemp = correctWord;
	        //0 is grey, yellow is 1, green is 2
	        int[] colorForLetter = new int[5]; 

	        //Check for correct letter and position (green)
	        for (int i = 0; i < 5; i++) { 
	            if (inputWord[i] == answerTemp[i]) {
	            	//Clear cell of answerTemp because we already found it.
	                answerTemp[i] = '-';
	                colorForLetter[i] = 2;
	            }
	        }

	        //Check for correct letter in wrong position (yellow)
	        for (int j = 0; j < 5; j++) { 
	            for (int k = 0; k < 5; k++){
	                if (inputWord[j] == answerTemp[k] && colorForLetter[j] != 2) {
	                    //If that letter is not already green and matches some other letter
	                    colorForLetter[j] = 1;
	                    //Clear cell of answerTemp because we already found it.
	                    answerTemp[k] = '-';
	                }
	            }
	        }

	        return colorForLetter;
	    }

	    //Word should be 5 characters long and contain only uppercase letters
	    public static boolean isValidInput(String input) {
	        return (input.length() == 5 && input.matches("[A-Z]+"));
	    }
	        
	}

	