package usflix;

import java.util.ArrayList;
import java.util.LinkedList;

public class User {

	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private ArrayList<MovieRating> seenMovies;
	
	public User(String f,String l,String u,String p){
		firstName=f;
		lastName=l;
		username=u;
		password=p;
		seenMovies=new ArrayList<MovieRating>();
	}
	
	public boolean login(String p){
		if(p.equals(password)){
			return true;
		}else{
			return false;
		}
	}
	
	public void addRating(Movie m, float r){
		boolean hasmovie=false;
		//if the movie is in the seenMovise
		for (int i=0;i<seenMovies.size();i++){
			if (seenMovies.get(i).getMovie().title.equals(m.title)){
				m.addRating(r);
				seenMovies.get(i).setRating(r);
				hasmovie=true;
			}
		}
		//if not
		if (hasmovie==false){
			m.addRating(r);
			seenMovies.add(new MovieRating(m,r));
		}
	}
	
	public String getRating(Movie m){
		//to check whether there is rating for this movie
		int num=-1;
		for (int i=0;i<seenMovies.size();i++){
			if (seenMovies.get(i).getMovie().title.equals(m.title)){
				num=i;
				break;
			}
		}
		//if not
		if (num==-1){
				return String.valueOf(m.getAverageRating()+"(expected)");
		
		}else{
			//if yes
			return String.valueOf(seenMovies.get(num).getRating());
		}
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public ArrayList<Movie> getSeenMovies(){
		//create a new arraylist to store the movies
		ArrayList<Movie> movies=new ArrayList<Movie>();
		for (int i=0;i<seenMovies.size();i++){
			movies.add(seenMovies.get(i).getMovie());
		}
		return movies;
	}
	
	public String getPassWord(){
		return password;
	}
	
	
	public String getUser(){
		return username;
	}

}
