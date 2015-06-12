package LyricFromFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Modified version of Lyric.java from class0124. Ask user for the file name, 
 * read 5 lines of lyric from the file, and print it to console.
 * 
 */
public class LyricFromFile {

	@SuppressWarnings("null")
	public static void main(String[] args) {
		// TODO Ask user for the file name
		// set up Scanner with keyboard input
		Scanner sc = new Scanner(System.in);
		// prompt user to give the file name
		System.out.print("Enter the file name: ");
		// read in the filename
		String filename = sc.nextLine();
		//TODO create a list to store the line
		String[] list = new String[5];
		try {
			Scanner filescan = new Scanner(new File(filename));
				// TODO Read the 5 lines of lyric from the file
				// for loop that repeats 5 times
				for (int i = 0; i < 5; i++) {
					// read 1 line from the file
					list[i] = filescan.nextLine();
					// TODO Print it to console.
				}
				// Separate from each time
			//to read it from list and print it for 3 times
			for (int j=0;j<3;j++){
				for (int i=0;i<5;i++){
					System.out.println(list[i]);
				}
				System.out.println(" ");
			}
		} catch (FileNotFoundException e) {
			System.out.println("No such file exists");
		}

	}

}