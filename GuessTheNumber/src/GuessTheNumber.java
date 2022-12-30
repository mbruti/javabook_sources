import java.util.Scanner;
public class GuessTheNumber {
	int numberToGuess;
	int tries=0;
	GuessTheNumber() {
		numberToGuess=(int)(Math.random()*100+1);
	}
	void start() {
		int guess;
		Scanner in = new Scanner(System.in);
		do {
			tries++;
			System.out.println("Insert your guess:");
			guess=in.nextInt();
			if (guess>numberToGuess) 
				System.out.println("Too high!");
			else if (guess<numberToGuess) 
				System.out.println("Too low!");
		} while (guess!=numberToGuess);
		System.out.println("You made it in "+tries+" attempts!");
		in.close();
	}
	public static void main(String[] args) {
		GuessTheNumber guessTheNumber= new GuessTheNumber();
		guessTheNumber.start();
	}
}
