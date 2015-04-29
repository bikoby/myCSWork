
public class Cobra extends Reptiles {
	public static final String scientificName="Felis silvestris catus"; 
	public final String nickName; 
	private float height; 
	private float weight; 

	public Cobra(String nickName){
		this.nickName = nickName;
		height=0;
		weight=0;
		totalReptiles++; 
		totalAnimals++; 

	}
	
	public void setHeight(float height){
		this.height=height;
		
	}
	
	public void setWeight(float weight){
		this.weight=weight;
		
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getWeight(){
		return weight;
	}
	
	public String toString(){
		return "scientificName: "+scientificName+"\n"+"nickName: "+ nickName+"\n"+"height:"+ height+"\n"+"weight:"+ weight; 

	}

}
