import java.util.ArrayList;


public class ArrayListTest {

	public static void main(String[] args) {
		ArrayList <Integer> a = new ArrayList <Integer> ();
		for (int i=1;i<11;i++){
			a.add(i);
		}
		a.add(0,1);
		System.out.print("[");
		for (int i=0;i<11;i++){
			System.out.print(" "+a.get(i));
			if(i<10){
				System.out.print(",");
			}
		}
		System.out.print("]");
		System.out.println(" ");
		
		a.set(0,0);
		System.out.print("[");
		for (int i=0;i<11;i++){
			System.out.print(" "+a.get(i));
			if(i<10){
				System.out.print(",");
			}
		}
		System.out.print("]");
		System.out.println(" ");
		
		for (int i=11;i<21;i++){
			a.add(i);
		}
		System.out.print("[");
		for (int i=0;i<21;i++){
			System.out.print(" "+a.get(i));
			if(i<20){
				System.out.print(",");
			}
		}
		System.out.print("]");
		System.out.println(" ");
		
		for (int i=0;i<a.size();i++){
			if((a.get(i) % 2)==0){
				a.remove(a.get(i));
			}
		}
		System.out.print("[");
		for (int i=0;i<a.size();i++){
			System.out.print(" "+a.get(i));
			if(i<a.size()-1){
				System.out.print(",");
			}
		}
		System.out.print("]");
		System.out.println(" ");
		
		for (int i=0;i<10;i++){
			a.add(a.get(i));
		}
		System.out.print("[");
		for (int i=0;i<a.size();i++){
			System.out.print(" "+a.get(i));
			if(i<a.size()-1){
				System.out.print(",");
			}
		}
		System.out.print("]");
		System.out.println(" ");
	
		ArrayList <Integer> b = new ArrayList <Integer> ();
		for (int i=0;i<a.size();i++){
			if(i%2!=0){
				b.add(a.get(i));
			}
		}
		System.out.print("[");
		for (int i=0;i<b.size();i++){
			System.out.print(" "+b.get(i));
			if(i<b.size()-1){
				System.out.print(",");
			}
		}
		System.out.print("]");
		System.out.println(" ");
	}
	
	

}
