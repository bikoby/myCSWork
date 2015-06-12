
public class CentralBeardedDragon extends Reptiles {
	public static final String scientificName="Pogona vitticeps"; 
	public final String nickName; 
	private float height; 
	private float weight; 

	public CentralBeardedDragon(String nickName){
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
