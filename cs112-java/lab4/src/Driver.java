
public class Driver {

	public static void main(String[] args) {
		
		Panda panda=new Panda("Pa");
		Cat cat=new Cat("Ca");
		CentralBeardedDragon centralBeardedDragon=new CentralBeardedDragon("Ce");
		panda.setHeight(160);
		panda.setWeight(70);
		cat.setHeight(20);
		cat.setWeight(10);
		centralBeardedDragon.setHeight(40);
		centralBeardedDragon.setWeight(20);
		System.out.println("totalAnimals:"+USFZoo.totalAnimals+"\n"+"totalMammals:"+ Mammals.totalMammals+"\n"+"totalReptile:"+Reptiles.totalReptiles );
		System.out.println(panda);
		System.out.println(cat);
		System.out.println(centralBeardedDragon);
	}

}
