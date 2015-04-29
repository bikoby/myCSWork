package usflix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieDatabase {

	private ArrayList<Movie> movies;
	
	public MovieDatabase(){
		movies=new ArrayList<Movie>();
	}
	
	public MovieDatabase(String filename){
		movies=new ArrayList<Movie>();
		Scanner scan;
		try {
			//to read all movies in movies file and save them in movies arraylist
			scan = new Scanner(new File(filename));
			while(scan.hasNextLine()){
				String title=scan.nextLine();
				int year=Integer.parseInt(scan.nextLine());
				String director=scan.nextLine();
				Movie m=new Movie(title,year,director);
				m.addRating(0);
				movies.add(m);
			}
		} catch (FileNotFoundException e) {
			System.out.println("movie file not found");
		}
		
	}
	
	public boolean addMovie(Movie m){
		try{
			movies.add(m);
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	public ArrayList<Movie> searchByTitle(String[] keywords){
		ArrayList<Movie> moviess=new ArrayList<Movie>();
		int wordmatch;
		//take out each movie
		for(int i=0;i<movies.size();i++){
			wordmatch=0;
			//to check whether each key word is in the movies' title
			for (int j=0;j<keywords.length;j++){
				if(movies.get(i).title.toLowerCase().contains(keywords[j].toLowerCase())){
					wordmatch++;
				}
			}
			//only when all the key words are in the movie's title, the movie will add in the list
			if(wordmatch==keywords.length){
				moviess.add(movies.get(i));
			}
		}
		return moviess;
	}

	public Movie getMovieByTitle(String title){
		for (int i=0;i<movies.size();i++){
			if(title.equals(movies.get(i).title)){
				return movies.get(i);
			}
		}
		return null;
	}
	
	public ArrayList<Movie> getMDB(){
		return movies;
	}
}
