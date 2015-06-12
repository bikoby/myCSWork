
import java.util.InputMismatchException;
import java.util.Scanner;


public class Prime {

	/**
	 * @param args
	 */
	public static void main(String[] args)  {
		System.out.println("Please input a number:");
		//TODO set up scanner to get a number of user
		Scanner scan = new Scanner(System.in);
		//TODO ask user to give a number
		int number = scan.nextInt();
		int a;
		//TODO check whether the number that the user typed in is a number 
		//try{
			 a=number-1;
			 	//TODO check whether the number is a positive number
				if (number>=0){
					
					int j=0;
					//TODO test if the given number n is prime
					for (int i=2;i<number;i++){
						if (number%i==0){
							System.out.println(number+" is not a prime");
							j++;
							i+=number;
						}
					}
					if (j==0){
						System.out.println(number+" is a prime");
					}
					
					// for each number x between 2 and (n-1) 
						// divide n by x
						// if the remainder is 0 then print "n is not prime"
					// print n is prime
				}else{
					System.out.println(number+" is not a prime");
				}
		//}catch(InputMismatchException e){
		//	System.out.println("Please in put a number!");
		//}
	}

}

