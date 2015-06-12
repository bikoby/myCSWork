package slotinterface;

public class FruitMachine implements SlotMachine{

	public int tokens=0;
	String[] fruit=new String[3];
	
	public FruitMachine(){
		fruit[0]="Apple";
		fruit[1]="Orange";
		fruit[2]="Pear";
	}
	

	public int addToken(int numTokens) {
		tokens += numTokens;
		return tokens;
	}


	public int pull() {
		int payout=0;
		int match=0;
		int[] random=new int[3];
		tokens--;
		String[] fruitType=new String[3];
		//to get three random fruits and print it out
		for (int i=0;i<fruitType.length;i++){
			random[i]=(int) (Math.random()*3);
			fruitType[i]=fruit[random[i]];
			System.out.print(fruitType[i]+" ");
		}
		System.out.println(" ");
		//to check how much does these fruits match
		if(fruitType[0].equals(fruitType[1])){
			match++;
		}
		if(fruitType[1].equals(fruitType[2])){
			match++;
		}
		if(fruitType[0].equals(fruitType[2])){
			match++;
		}
		payout=match;
		tokens+=payout;
		return payout;
	}

	public int endTheGame() {
		int result=tokens;
		tokens=0;
		return result;
	}

}
