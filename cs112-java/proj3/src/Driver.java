import java.util.InputMismatchException;
import java.util.Scanner;


public class Driver {

	public static void main(String[] args) {
		String filename;
		Scanner keyboard=new Scanner(System.in);
		//to check whether the length of args is empty
		if(args.length==0){
			System.out.print("Please enter the movie database file name:");
			filename=keyboard.nextLine();
		}else{
			filename=args[0];
		}
		//pass the filename to MovieDatatbase by creating a object
		MovieDatabase search=new MovieDatabase(filename);
		System.out.println("Welcome to USFlix!");
		//choose the way to search
		System.out.println("Please choose the type:(1.search by title. 2.search by director.)");
		int type=0;
		//check whether the user type in 1 or 2, if not, the loop will keep on
		while(type==0){
			try{
				System.out.print("Enter 1 or 2:");
				Scanner typechoose=new Scanner(System.in);
				type=typechoose.nextInt();
				if(type==1||type==2){
					break;
				}else{
					type=0;
				}
			}catch (InputMismatchException e){
				System.out.println("Please typn in 1 or 2 !");
			}
		}
		System.out.print("Enter keywords:");
		Scanner keywordscan=new Scanner(System.in);
		String keywords=keywordscan.nextLine().toLowerCase();
		String[] kw = keywords.split(" ");
		//pass keywords to different method according to the number type
		if(type==1){
			search.searchByTitle(kw);
		}else{
			search.searchByDirector(kw);
		}
	}

}
