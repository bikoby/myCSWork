
public class Movie {
	private String title;
	private int year;
	private String genre;
	private String director;
	private String release_day;
	
	public Movie(String t,int y,String g,String d,String r){
		title = t;
		year = y;
		genre = g;
		director = d;
		release_day = r;
	}
	
	public String GetterTitle() {
		return title;
	}
	
	public int GetterYear() {
		return year;
	}
	
	public String GetterGenre() {
		return genre;
	}
	
	public String GetterDirector() {
		return director;
	
	}public String GetterReleaseday() {
		return release_day;
	}
	
}
