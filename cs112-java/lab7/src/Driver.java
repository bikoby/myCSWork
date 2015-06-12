import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;


public class Driver {
	
	public static void main(String[] args) {
		Scanner scan=null;
		Scanner input=new Scanner(System.in);
		String filename=null;
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
				System.out.println("Please input a file name:");
				filename=input.nextLine();
			}
		}
		int row=scan.nextInt();
		int column=scan.nextInt();		
		double[][] ratings=new double[row][column];
		int user=0;
		//read the file and put all the data in the array "ratings"
		while(scan.hasNextDouble()){
			for (int i=0;i<column;i++){
				ratings[user][i]=scan.nextDouble();
			}
			//change to the next user
			user++;
		}
		double[][] compareRatings=new double[row][row];
		double sum;
		int[][] watchMovie=new int[row][row];
		//to compare the ratings with each other users
		for(int user1=0;user1<row;user1++){
			for (int user2=0;user2<row;user2++){
				sum=0;
				for (int rating=0;rating<column;rating++){
					if (ratings[user2][rating]!=-1&&ratings[user1][rating]!=-1){
						sum+=Math.abs(ratings[user2][rating]-ratings[user1][rating]);
						watchMovie[user1][user2]++;
					}
				}
				compareRatings[user1][user2]=(sum/watchMovie[user1][user2]);
			}
		}
		//to set the rating of compare itself to largest, so itself won't be the most similar user
		for (int i=0;i<row;i++){
			compareRatings[i][i]=5*row+1;
		}
		int minindex;
		double min;
		//create a LinkedList to store the similar user
		LinkedList<Integer> similar=new LinkedList<Integer>();
		//compare the compareRatings of each user, print out the lowest compareRating user
		for(int i=0;i<row;i++){
			minindex=0;
			min=compareRatings[i][0];
			similar.add(minindex);
			for(int j=1;j<row;j++){
				if(min>compareRatings[i][j]){
					min=compareRatings[i][j];
					minindex=j;
					similar.clear();
					similar.add(j);
				}else if(min==compareRatings[i][j]){
					similar.add(j);
				}
			}
			System.out.print("user_"+i+" most similar to:");
			for(Integer u:similar){
				System.out.print("user_"+u+",");
			}
			similar.clear();
			System.out.println("");
		}
		if(filename.equals("newrating.txt")){
			//extra credict part
			System.out.println("extra credict part:");
			int minindex1;
			double min1;
			//create a LinkedList to store the similar user
			LinkedList<Integer> similar1=new LinkedList<Integer>();
			//compare the compareRatings of each user, print out the lowest compareRating user
			for(int i=0;i<row;i++){
				minindex1=0;
				min1=compareRatings[i][0];
				similar1.add(minindex1);
				for(int j=1;j<row;j++){
					if(min1>compareRatings[i][j]){
						min1=compareRatings[i][j];
						minindex1=j;
						similar1.clear();
						similar1.add(j);
					}else if(min1==compareRatings[i][j]){
						if(watchMovie[i][minindex1]<watchMovie[i][j]){
							minindex=j;
							similar1.clear();
							similar1.add(j);
						//if the number of watchMovie and the compare rating are the same. add to the linked list
						}else if(watchMovie[i][minindex1]==watchMovie[i][j]){
							similar1.add(j);
						}
					}
				}
				System.out.print("user_"+i+" most similar to:");
				for(Integer u:similar1){
					System.out.print("user_"+u+",");
				}
				similar1.clear();
				System.out.println("");
			}
		}
	}

}
