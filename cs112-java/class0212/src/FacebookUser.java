import java.util.Scanner;


public class FacebookUser {
		String username;
		String password;
		String sex;
		int age;
		public FacebookUser(String user, String pwd,String s, int a) {
			username = user;
			password = pwd;
			age = a;
			sex = s;
			
			}
		public static class Driver{
			public static void main(String[] args) {
				FacebookUser[] user = new FacebookUser[4];
				user[0] = new FacebookUser("a", "a","male",15);
				user[1] = new FacebookUser("b", "b","female",16);
				user[2] = new FacebookUser("c", "c","male",17);
				user[3] = new FacebookUser("d", "d","female",18);
				System.out.println("Enter the user name: ");
				Scanner sc =new Scanner(System.in);
				String un = sc.nextLine();
				FacebookUser us = new FacebookUser(" "," "," ",0);
				for (int i=0;i<user.length;i++){
					if (un.equals(user[i].username)){
						us = user[i];
						break;
					}else{
						System.out.println("There is no such user");
					}
				}
				System.out.println("Enter the password: ");
				String pwd = sc.nextLine();
				if (pwd.equals(us.password)){
					System.out.println("Welcome, "+us.username);
				}else{
					System.out.println("Your password is wrong!");
				}
			
			}

		}

	
}
