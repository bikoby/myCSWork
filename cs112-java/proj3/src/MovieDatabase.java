import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class MovieDatabase {

	private ArrayList <Movie> movies = new ArrayList<Movie>();
	
	public void addMovie(Movie m){
		movies.add(m);
	}
	
	public MovieDatabase(String filename){
		boolean loop=true;
		String title;
		int year;
		String director;
		//to make sure the filename is exist
		while(loop==true){
			try {
				//read the file, create movie objects and store them into the arraylist movies
				Scanner file = new Scanner(new File(filename));
				loop=false;
				while(file.hasNextLine()){
					title=file.nextLine();
					year=Integer.parseInt(file.nextLine());
					director=file.nextLine();
					Movie movie = new Movie(title,year,director);
					addMovie(movie);
				}
				
			} catch (FileNotFoundException e) {
				//if the file name is not exist, ask the user to type in a new file name
				Scanner file =new Scanner(System.in);
				System.out.println("file is not exit");
				System.out.println("Please enter the movie database file name:");
				filename=file.nextLine();
			}
		}
		
	}
	
	public void searchByTitle(String[] keyword){
		boolean match = false;
		int wordmatch;
		//take out each movie
		for(int i=0;i<movies.size();i++){
			wordmatch=0;
			//to check whether each key word is in the movies' title
			for (int j=0;j<keyword.length;j++){
				if(movies.get(i).title.toLowerCase().contains(keyword[j])){
					wordmatch++;
				}
			}
			//only when all the key words are in the movie's title, the movie will print out
			if(wordmatch==keyword.length){
				System.out.println(movies.get(i).toString());
				match=true;
			}
		}
		if(match==false){
			System.out.println("No results found");
		}
	}
	
	//almost the same as the searchByTitle
	public void searchByDirector(String[] keyword){
		boolean match = false;
		int wordmatch;
		for(int i=0;i<movies.size();i++){
			wordmatch=0;
			for (int j=0;j<keyword.length;j++){
				if(movies.get(i).director.toLowerCase().contains(keyword[j])){
					wordmatch++;
				}
			}
			if(wordmatch==keyword.length){
				System.out.println(movies.get(i).toString());
				match=true;
			}
		}
		if(match==false){
			System.out.println("No results found");
		}
	}



}
