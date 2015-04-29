import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Driver {

	public static void main(String[] args) {	
		//TODO scan all the movie from the file
		try {
			Scanner sc_m = new Scanner(new File("movies lab2.txt"));
			Movie[] movie = new Movie[10];
			//set up a number to mark down the number of movies
			int movienumber=0;
			String title;
			int year;
			String genre;
			String director;
			String releasday;
			//store movies in movie array
			while (sc_m.hasNextLine()){
				//before each movie, there is a empty line
				sc_m.nextLine();
				title=sc_m.nextLine();
				year=Integer.parseInt(sc_m.nextLine());
				genre=sc_m.nextLine();
				director=sc_m.nextLine();
				releasday=sc_m.nextLine();
				movie[movienumber] = new Movie(title,year,genre,director,releasday);
				movienumber++;
			}
			//TODO get the key word from the user
			Scanner sc_kw = new Scanner(System.in);
			System.out.print("Welcome to USFlix!\nEnter keywords:");
			String keyword=sc_kw.nextLine().toLowerCase();
			int matchnumber = 0;
			for(int i=0;i<movienumber;i++){
				//if keyword is in the title of the movie, then print out the information of it
				if ( movie[i].GetterTitle().toLowerCase().contains(keyword)){
					System.out.println(movie[i].GetterTitle()+"("+movie[i].GetterYear()+")");
					System.out.println("Genre: " +movie[i].GetterGenre());
					System.out.println("Director: " +movie[i].GetterDirector());
					System.out.println("Release Day: " +movie[i].GetterReleaseday());
					System.out.println("");
					matchnumber += 1;
				}
			}
			if(matchnumber==0){
				System.out.println("No results found");
			}
		} catch (FileNotFoundException e) {
			System.out.println("movies lab2.txt is missing!");
		}
	}
}
