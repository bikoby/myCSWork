
public class Panda extends Mammals {
	public static final String scientificName="Ailuropoda melanoleuca"; 
	public final String nickName; 
	private float height; 
	private float weight; 

	public Panda(String nickName){
		this.nickName = nickName;
		height=0;
		weight=0;
		totalMammals++; 
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
