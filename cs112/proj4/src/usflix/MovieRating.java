package usflix;

public class MovieRating {

	private Movie movie;
	private float rating;
	
	public MovieRating(Movie m,float r){
		movie=m;
		rating=r;
	}

	public String toString(){
		if (rating>0){
			return rating+"";
		}else{
			return movie.getAverageRating()+"(average)";
		}
	}
	
	public Movie getMovie(){
		return movie;
	}
	
	public float getRating(){
		return rating;
	}
	
	public void setRating(float r){
		rating=r;
	}
}
