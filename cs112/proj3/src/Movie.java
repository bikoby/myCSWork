
public class Movie {

	protected final String title;
	protected final int year;
	protected final String director;
	
	public Movie(String title,int year,String director){
		this.title = title;
		this.year = year;
		this.director = director;
	}
	
	
	public String toString(){
		return title+" ("+year+")"+"\n"+"Director: "+director;
	}
	

}
