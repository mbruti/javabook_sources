 /** Implementa una semplice calcolatrice con i 4 operatori fondamentali
 * più percentuale (%) e elevamento a potenza (p)
 * <p>Esempio: <code>java Calc 2 * 10</code>   Restituisce 20.
 * <p>Esempio: <code>java Calc 2 p 3</code>    Restituisce 8.
 * <p>Esempio: <code>java Calc 150 % 30</code> Restituisce 45.
 *  
 * @author Marco Bruti
 * 
 */
public class Calc {
    static double num1,num2,risultato;
    static char operatore;
    static int ch;
    static boolean erroreOperatore=false;

    /** Entry point del programma
    *  
    * @param args	first number,operator, second number
    */
	public static void main(String[] args) throws java.lang.NumberFormatException {
		if (args.length<3) {
			System.out.print("Almeno 3 argomenti");
			System.exit(-1);
		}
		try {
			num1=Double.parseDouble(args[0]);
			num2=Double.parseDouble(args[2]);
			operatore=args[1].charAt(0);
			switch (operatore) {
				case '+': risultato=num1+num2;break;
				case '-': risultato=num1-num2;break;
				case 'x': risultato=num1*num2;break;
				case '/': risultato=num1/num2;break;
				case 'p': risultato=Math.pow(num1, num2);break;
				case '%': risultato=num1*(num2/100);break;
				default: erroreOperatore=true;
			};
		} catch (java.lang.NumberFormatException nfe) {
			System.out.print("Errore nel formato numerico.\n");
			System.exit(-1);
		}
		if (erroreOperatore) {
			System.out.print("Operatori permessi: + - x (prodotto) / p (potenza) % (percentuale).\n");
		} else {
			System.out.print("Risultato: "+num1+" "+operatore+" "+num2+" = "+risultato);
		}
	}
}