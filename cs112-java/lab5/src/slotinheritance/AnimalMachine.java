package slotinheritance;

public class AnimalMachine extends SlotMachine{

	String[] animal=new String[3];
	
	public AnimalMachine(){
		animal[0]="Cat";
		animal[1]="Dog";
		animal[2]="Pig";
	}

	public int pull() {
		int match=0;
		int[] random=new int[3];
		tokens--;
		String[] animalType=new String[3];
		//to get three random animals and print it out
		for (int i=0;i<animalType.length;i++){
			random[i]=(int) (Math.random()*3);
			animalType[i]=animal[random[i]];
			System.out.print(animalType[i]+" ");
		}
		System.out.println(" ");
		//to check how much does these animals match
		if(animalType[0].equals(animalType[1])){
			match++;
		}
		if(animalType[1].equals(animalType[2])){
			match++;
		}
		if(animalType[0].equals(animalType[2])){
			match++;
		}
		payout=match;
		tokens+=payout;
		return payout;
	}


}
