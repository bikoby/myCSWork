package slotinheritance;

public class ColorMachine extends SlotMachine{

	String[] fruit=new String[3];
	
	public ColorMachine(){
		fruit[0]="Red";
		fruit[1]="Orange";
		fruit[2]="Blue";
	}

	public int pull() {
		int match=0;
		int[] random=new int[3];
		tokens--;
		String[] colorType=new String[3];
		//to get three random colors and print it out
		for (int i=0;i<colorType.length;i++){
			random[i]=(int) (Math.random()*3);
			colorType[i]=fruit[random[i]];
			System.out.print(colorType[i]+" ");
		}
		System.out.println(" ");
		//to see how much colors does these match
		if(colorType[0].equals(colorType[1])){
			match++;
		}
		if(colorType[1].equals(colorType[2])){
			match++;
		}
		if(colorType[0].equals(colorType[2])){
			match++;
		}
		payout=match;
		tokens+=payout;
		return payout;
	}


}
