import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Driver {

	public static void main(String[] args) {
		
		//I have done the part of extra credit
		
		//TODO scan all the movie from the file
		try {
			Scanner sc_m = new Scanner(new File("movies.txt"));
			String[][] list_m = new String[148][10];
			//store movies in list_m separately
			for (int i=0; i<list_m.length;i++){
				String[] mv =sc_m.nextLine().split(" "); 
				for (int j=0;j<mv.length;j++){
					list_m[i][j]=mv[j];
				}
			}
			
			//TODO get the key word from the user
			Scanner sc_kw = new Scanner(System.in);
			System.out.print("Welcome to USFlix!\nEnter keywords:");
			String keyword=sc_kw.nextLine();
			String[] kw = keyword.split(" ");
			//TODO to compare the keyword and the movies
			int[] match=new int[148];
			int i1=0;
			int i2=0;
				for (int j=0;j < list_m.length; j++){
					i2=0;
					for (int k=0;k<list_m[j].length;k++){
						for (int i=0;i<kw.length;i++){
							if (list_m[j][k]==null){
								break;
							}
							if (kw[i].equals(list_m[j][k]) || kw[i].equals(list_m[j][k].toLowerCase())){
								i2++;
								if(i2==kw.length){
									match[i1]=j;
									i1++;
									i2=0;
								}
							}
						}
					}
			}
			for (int j=0;j < i1; j++){
				for (int k=0;k<list_m[match[j]].length;k++){
					if (list_m[match[j]][k]==null){
						break;
					}
					System.out.print(list_m[match[j]][k]+" ");
				}
				System.out.println(" ");
			}
		} catch (FileNotFoundException e) {
			System.out.println("movies.txt is missing!");
		}
	}
}
