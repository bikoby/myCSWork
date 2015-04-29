import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Driver {

	public static void main(String[] args) {
		Scanner scan=null;
		Scanner input=new Scanner(System.in);
		String filename=null;
		ArrayList<String> words= new ArrayList<String>();
		//to check whether args is empty
		if(args.length>0){
			filename=args[0];
		}else{
			System.out.println("Please input a file name:");
			filename=input.nextLine();
		}
		//to check whether the file exist 
		while(scan==null){
			try {
				scan=new Scanner(new File(filename));
			} catch (FileNotFoundException e) {
				System.out.println("Please a file name:");
				filename=input.nextLine();
			}
		}
		//to scan all words in the file
		while(scan.hasNext()){
			String oldWord=scan.next().toLowerCase();
			String word="";
			//to add words in array list "words" without any punctuation
			for (int i=0;i<oldWord.split("[^a-zA-Z]").length;i++){
					word+=oldWord.split("[^a-zA-Z]")[i];
			}
			//to check whether there is empty word
			if (!word.equals("")){
				words.add(word);
			}
		}
		
		//iterative counter method
		Iterative i=new Iterative();
		Word[] Iwordlist=i.iterativeCounter(words);
		Sorting.selectionSort(Iwordlist);
		System.out.println("Iterative Counter Result:");
		for (Word word:Iwordlist){
			System.out.println(word);
		}
		//recursive counter method
		Recursive r=new Recursive();
		Word[] Rwordlist=r.recursiveCounter(words);
		Sorting.selectionSort(Rwordlist);
		System.out.println("Recursive Counter Result:");
		for (Word word:Rwordlist){
			System.out.println(word);
		}
		
	}

}
