import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Login {

	public static void main(String[] args) {
		// TODO ask the user to type in user name
		// set up Scanner with keyboard input
				Scanner sc = new Scanner(System.in);
		//create a 'i' to record the time of wrong try
				//int i=0;
		//set up a loop to give user 3 times to try
				for (int j=0; j < 3; j++){
						try {
							// prompt user to give the file name
							System.out.println("Enter the user name: ");
							// read in the filename
							String filename = sc.nextLine();
							Scanner filescan = new Scanner(new File(filename));
							// prompt user to give the password
							System.out.println("Enter the password: ");
							String password = sc.nextLine();
							//check whether the password is matched
							if (password.equals(filescan.nextLine())){
								System.out.println("Welcome, "+filename);
								break;
							}else{
								System.out.println("Your password is wrong!");
								//i++;
							}
							
						} catch (FileNotFoundException e) {
							//if there is no such user name
							System.out.println("There is no such user");
							//i++;
						}
							//tell the user how many time he still can try
							if (j==0){
									System.out.println("You still have 2 times to try.");
							}
							if (j==1){
									System.out.println("You still have 1 more time to try");
							}
							if (j==2){
									System.out.println("You was wrong for 3 times. Go away!");		
							}
						System.out.println(" ");
				}
		//if the i equals 3, the program terminate
				//if (i==3){
					//System.out.println("You was wrong for 3 times. Go away!");
				//}
	}

}
