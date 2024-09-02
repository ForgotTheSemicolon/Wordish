OVERVIEW
Wordish is a simple Wordle clone with a Web backend. It is divided into two projects, one for the GUI and one for the server portion. Most of the logic resides in the front end, with the server portion returning random words via REST endpoints.
Theoretically, the back end could be used for any application that needs to pick random five-letter words.
Right now both the GUI and backend are intended to be run on the same computer for demonstration purposes.

USAGE
To play, run the WordController application, then launch WordishGui. Enter a five-letter word and click the Enter button or press Enter on the keyboard. Each letter of your guess will be colored based on whether or not it's present in a randomly-selected mystery word.
Green means you've guessed the right letter in the right position, yellow means the right letter in the wrong position, and black means the letter isn't present in the mystery word at all. You have six tries to guess the word. 
Unlike Wordle, a new word is selected every time you play. Good luck!
