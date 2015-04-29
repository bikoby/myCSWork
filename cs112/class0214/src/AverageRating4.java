import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class AverageRating4 {
	
	private static final int maxRating = 5;
	private static final float minRating = 0;


	public static void main(String[] args) {

		int n = 0;
		boolean l=true;
		while (l){
			// TODO Ask user for the file name
			// create a Scanner object to read keyboard input
			Scanner scan = new Scanner(System.in);
			System.out.print("Enter file name:");
			String filename = scan.next();
			
			try {
				Scanner filescan = new Scanner(new File(filename));
				float sum=0;
				while (filescan.hasNextFloat()) {
					float number = filescan.nextFloat();
					if (number<=maxRating && number>=minRating){
						System.out.println(number);
						sum += number ;
						n++;
					}
				}
				System.out.println("\nThe sum is "+sum);
				System.out.println("The average is "+sum/n);

				l=false;
			} catch (FileNotFoundException e) {
				System.out.println("\nFile not found");
			}
		}

	}

}

