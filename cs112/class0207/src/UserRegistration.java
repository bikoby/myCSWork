import java.util.Scanner;


public class UserRegistration {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		//while loop until the username is right
		int b1=0;
		String username = new String();
		while (b1==0){
			//TODO check the first letter in username whether it is a letter
			System.out.println("Create a username. A username contains letters and/or numbers.");
			String username1 = scan.nextLine();
			char[] unArray = username1.toCharArray();
			if ((int)unArray[0] >= (int)'A' && (int)unArray[0] <= (int)'Z' || (int)unArray[0] >= (int)'a' &&(int)unArray[0] <= (int)'z') {
				b1=1;
				username=username1;
			}else{
			    b1=0;
			    System.out.println("Your username: " +username1+ ". Sorry, invalid username. Try again.\n");
			}
		}
		System.out.println("Your username: " +username+". ok!");
		char[] unArray = username.toCharArray();
		//while loop to check the password until it is right
		int check_number=0,check_uletter=0,compare=0,check_l=0;
		String password1=new String();
		while (check_number==0||check_uletter==0||compare==0||check_l==0){
			System.out.println("Now password. A password contains letters and numbers.\nIt needs at least one number and at least one uppercase letter.\nIt also needs to be at least 8 characters long, and cannot contain username.");
			String password = scan.nextLine();
			//TODO check whether the password has letters and numbers
			char[] pwdArray = password.toCharArray();
			//to check whether there is a number in password
			for (int i = 0; i < pwdArray.length ; i++)
				if ((int)pwdArray[i] <= (int)'9' && (int)pwdArray[i] >= (int)'0'){
					check_number=1;	
					break;
				}else{
				    check_number=0;
				}
			//to check whether there is a Upper letter
			for (int i = 0; i < pwdArray.length ; i++)
				if ((int)pwdArray[i] <= (int)'Z' && (int)pwdArray[i] >= (int)'A'){
					check_uletter=1;	
					break;
				}else{
					check_uletter=0;
				}
			if (check_number==0 || check_uletter==0){
				System.out.println("Your password:"+password+". Password must consist of at least one Upper letter and one number\n");
				
			}
			//to check the password's length
			if (password.length() < 8 ){
				check_l=0;
			}else{
				check_l=1;
			}
			if(check_l==0){
				System.out.println("The length of password must longer than 8\n");
			}
			//to compare the password and user name
			for (int i=0; i<unArray.length	;i++){
				for (int j=0;j<pwdArray.length;j++){
					if (unArray[i]==pwdArray[j]){
						compare=0;
						break;
					}else{
						compare =1;
					}
				}
				if (compare==1){
					break;
				}
					
			}
			if (compare==0){
				System.out.println("Username cannot be a part of password\n");
			}
			password1=password;
		}
		System.out.println("Your password:"+password1+". Ok! Your account is now set up. ");
	}

}
