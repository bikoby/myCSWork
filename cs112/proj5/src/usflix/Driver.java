package usflix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Driver class manages user interaction: input and output between the program
 * and users
 * 
 * @author EJ Jung
 * @version 2.0 March 3, 2013.
 */
public class Driver {

	/**
	 * There will be only one movie database and one user database in the system,
	 * so they are declared as static.
	 */
	private static MovieDatabase movieDB;
	private static UserDatabase userDB;

	/**
	 * The first program argument (args[0]) may contain the movie information
	 * file name, and the second program argument (args[1]) may contain the user
	 * information file name.
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);

		userDB = new UserDatabase();

		if (args.length > 1) {
			movieDB = new MovieDatabase(args[0]);
			try {
				loadUsers(args[1]);
			} catch (FileNotFoundException e) {
				System.out.println("User file not found");
				e.printStackTrace();
			}
		} else if (args.length > 0) {
			movieDB = new MovieDatabase(args[0]);
		} else {
			System.out.print("Please enter the movie database file name: ");
			movieDB = new MovieDatabase(scan.nextLine());
		}

		int choice = -1;
		while (choice != 0) {
			try {
				System.out
						.println("Welcome to USFlix! Select an option from the menu.");
				System.out
						.println("1 to load users and their ratings from a file");
				System.out.println("2 to login");
				System.out.println("3 to create a new account");
				System.out.println("0 to quit");
				System.out.print("Enter your choice: ");
				choice = Integer.parseInt(scan.nextLine());

				switch (choice) {
				case 0:
					System.out.println("Bye!");
					break;
				case 1:
					System.out.print("Enter the file name: ");
					loadUsers(scan.nextLine());
					break;
				case 2:
					System.out.print("Enter the username: ");
					String username = scan.nextLine();
					System.out.print("Enter the password: ");
					String password = scan.nextLine();
					User u = userDB.login(username, password);
					if (u == null)
						System.out.println("Login error");
					else {
						userMenu(u);
					}
					break;
				case 3:
					System.out.print("Enter your first name: ");
					String firstName = scan.nextLine();
					System.out.print("Enter your last name: ");
					String lastName = scan.nextLine();
					System.out.print("Enter a username: ");
					username = scan.nextLine();
					System.out.print("Enter a password: ");
					password = scan.nextLine();
					//TODO: account creation
					userDB.createAccount(firstName, lastName, username, password);
					break;
				}

			} catch (InputMismatchException e) {
				System.out.println("Invalid choice");
			} catch (FileNotFoundException e) {
				System.out.println("User file is not found");
			}
		}
	}

	/**
	 * loadUsers() method loads the user information from a file. If the username
	 * is available, then the new account is created and his or her rating information 
	 * gets added to the movie database and also to the user object. If the username
	 * is not available, then this method skips to the next user information (until it
	 * sees "done"). 
	 * 
	 * @param filename the user information file name
	 * @throws FileNotFoundException if the user information file is not found, 
	 * then the main method catches the exception and asks user for another file name.
	 */
	private static void loadUsers(String filename) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));

		while (scan.hasNextLine()) {
			//TODO: account creation
			String firstname=scan.nextLine();
			String lastname=scan.nextLine();
			String username=scan.nextLine();
			String password=scan.nextLine();
			User u=userDB.createAccount(firstname, lastname, username, password);
			String title=scan.nextLine();
			//if title equals to "done", the movies loading of this user is end and turn to the next user 
			while (!title.equals("done")) {
				Movie m = movieDB.getMovieByTitle(title);
				if (m == null) {
					m = new Movie(title, 0, null);
					movieDB.addMovie(m);
				}
				String tmp = scan.nextLine();
				u.addRating(m, Float.parseFloat(tmp));
				title = scan.nextLine();
			}
		}

	}

	/**
	 * userMenu handles menu options that are available after logging in,
	 * such as search by title (and director if you have it) and the
	 * list of movies the user has seen before. 
	 * 
	 * @param u 
	 */
	private static void userMenu(User u) {
		Scanner scan = new Scanner(System.in);
		int choice = -1;
		while (choice != 0) {
			System.out.println("Welcome, " + u.getFirstName()
					+ "! Select an option from the menu.");
			System.out.println("1 to search movies by title");
			System.out
					.println("2 to see the list of movies you have seen before");
			System.out.println("3 to see the recommanded movies");
			System.out.println("0 to logout");
			System.out.print("Enter your choice: ");
			choice = Integer.parseInt(scan.nextLine());
			switch (choice) {
			case 1:
				System.out.print("Enter keywords: ");
				String[] keywords = scan.nextLine().split(" ");

				ArrayList<Movie> searchResults = movieDB
						.searchByTitle(keywords);

				listMenu(u, searchResults);
				break;
			case 2:
				listMenu(u, u.getSeenMovies());
				break;
			case 3:
				//save the movie with the index
				HashMap<Movie,Integer> mDB=new HashMap<Movie,Integer>();
				//create 2 hashmap for user in order to get the user known the index of it as well as get the
				//index known the user
				HashMap<User,Integer> uDB=new HashMap<User,Integer>();
				HashMap<Integer,User> uDB2=new HashMap<Integer,User>();
				double[][] ratings=new double[userDB.getUserDB().size()][movieDB.getMDB().size()]; 
				int userN=0;
				int movieN=0;
				for(User user: userDB.getUserDB().values()){
					uDB.put(user,userN);
					uDB2.put(userN, user);
					ArrayList<Movie> movies=user.getSeenMovies();
					for (Movie movie:movies){
						//if the movie is not rated, add it into the mDB
						if(!mDB.containsKey(movie)){
							mDB.put(movie,movieN);
							ratings[userN][movieN]=Double.parseDouble(user.getRating(movie));
							movieN++;
						}else{
							//if it is rated, add another rating in it
							int index=mDB.get(movie);
							ratings[userN][index]=Double.parseDouble(user.getRating(movie));
						}
					}
					userN++;
				}
				for (int i=0;i<userN;i++){
					for(int j=0;j<movieN;j++){
						//to set the ratings of movies, that the user have not seen, to -1
						if(ratings[i][j]==0){
							ratings[i][j]=-1;
						}
					}
				}
				double[][] compareRatings=new double[userN][userN];
				double sum;
				int[][] watchMovie=new int[userN][userN];
				//to compare the ratings with each other users
				for(int user1=0;user1<userN;user1++){
					for (int user2=0;user2<userN;user2++){
						if(user1!=user2){
							sum=0;
							for (int rating=0;rating<movieN;rating++){
								if (ratings[user2][rating]!=-1&&ratings[user1][rating]!=-1){
									sum+=Math.abs(ratings[user2][rating]-ratings[user1][rating]);
									watchMovie[user1][user2]++;
								}
							}
							if(watchMovie[user1][user2]==0){
								compareRatings[user1][user2]=5;
							}else{
								compareRatings[user1][user2]=(sum/watchMovie[user1][user2]);
							}
						}
					}
				}
				//to set the rating of compare itself to largest, so itself won't be the most similar user
				for (int i=0;i<userN;i++){
					compareRatings[i][i]=5;
				}
				double min1;
				//create a LinkedList to store the similar user
				LinkedList<User> similar1=new LinkedList<User>();
				//compare the compareRatings of each user, print out the lowest compareRating user
				int index=uDB.get(u);
				min1=compareRatings[index][0];
				int minindex=0;
				similar1.add(uDB2.get(0));
				for(int j=1;j<userN;j++){
					if(j!=index){
						if(min1>compareRatings[index][j]){
							min1=compareRatings[index][j];
							minindex=j;
							similar1.clear();
							similar1.add(uDB2.get(j));
						}else if(min1==compareRatings[index][j]){
							if(watchMovie[index][minindex]<watchMovie[index][j]){
								similar1.clear();
								similar1.add(uDB2.get(j));
							//if the number of watchMovie and the compare rating are the same. add to the linked list
							}else if(watchMovie[index][minindex]==watchMovie[index][j]){
								similar1.add(uDB2.get(j));
							}
						}
					}
				}
				ArrayList<Movie> recommandM=new ArrayList<Movie>();
				for (User user:similar1){
					for (Movie m1:user.getSeenMovies()){
						//to get the movies that u have not seen and user have seen and his rating for the movie
						//is larger than 3
						if(!(u.getSeenMovies().contains(m1))&&Float.parseFloat(user.getRating(m1))>=3){
							if(!recommandM.contains(m1)){
								recommandM.add(m1);
							}
						}
					}
				}
				listMenu(u,recommandM);
				break;
			case 0:
				return;
			}
		}
	}

	/**
	 * listMenu handles printing the list of Movies with the appropriate
	 * rating (user's own if available, average otherwise), letting user
	 * rate any of the Movies in the list.  
	 * 
	 * @param u
	 * @param list search result or the list of movies user has seen
	 */
	private static void listMenu(User u, ArrayList<Movie> list) {
		Scanner scan = new Scanner(System.in);
		for (int i = 0; i < list.size(); i++) {
			Movie m = list.get(i);
			System.out.println((i + 1) + ". " + m +"  " +u.getRating(m));
		}
		int choice = -1;
		while (choice != 0) {
			System.out.println("Select the movie number to rate or watch");
			System.out.println("0 to go back to previous menu");
			System.out.print("Enter your choice: ");
			choice = Integer.parseInt(scan.nextLine());
			if (choice == 0) {
				return;
			} else if (choice - 1 >= 0 && choice <= list.size()) {
				Movie m = list.get(choice - 1);
				System.out.println("How did you like " + m.title
						+ "? (0.5~5 stars)");
				u.addRating(m, Float.parseFloat(scan.nextLine()));
			}
		}
	}
}

