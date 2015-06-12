package usflix;

import java.util.ArrayList;

public class Movie {

	protected final String title;
	protected final int year;
	protected final String director;
	private ArrayList<Float> ratings;
	
	public Movie(String title,int year,String director){
		this.title=title;
		this.year=year;
		this.director=director;
		ratings=new ArrayList<Float>();
	}
	
	public String toString(){
		return title+"("+year+")"+director;
	}

	public float getAverageRating() {
		float sum=0;
		float num=0;
		//to sum up all the rating
		for (int i=0;i<ratings.size();i++){
			if (ratings.get(i)!=0){
				sum+=ratings.get(i);
				num++;
			}
		}
		if(num==0){
			return 0;
		}
		return sum/num;
	}
	
	public void addRating(float r){
		ratings.add(r);
	}
	
	public void removeRating(float r){
		ratings.remove(r);
	}
		
	
}
