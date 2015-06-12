import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Histogram {

	public static void main(String[] args) {
		//numbers of reviews
		int number = 0;
		double[] star = new double[5];
		boolean loop=true;
		//while the file is exist, loop will become false and won't repeat again
		while(loop==true){
			try {
				Scanner scan = new Scanner(new File (args[0]));
				loop = false;
				while(scan.hasNextDouble()){
					double rating = scan.nextDouble();
					//only when the number is in the range of 0-5, the number will plus 1.
					if(rating>=0 && rating<=1){
						star[0]+=1;
						number++;
					}if(rating>1&&rating<=2){
						star[1]+=1;
						number++;
					}if(rating>2&&rating<=3){
						star[2]+=1;
						number++;
					}if(rating>3&&rating<=4){
						star[3]+=1;
						number++;
					}if(rating>4&&rating<=5){
						star[4]+=1;
						number++;
					}
				}
				
				System.out.println(number+" reviews");
				for(int i=5;i>0;i--){
					System.out.print((i-1)+"-"+i+" star:");
					//asterisks
					for(int j=0;j<star[i-1];j++){
						System.out.print("*");
					}
					//space after the stars
					for(int j=0;j<10-star[i-1];j++){
						System.out.print(" ");
					}
					//numbers of asterisks
					System.out.println("("+(int)star[i-1]+")");
				}
			} catch (FileNotFoundException e) {
				System.out.println(args[0]+" is not found !");
				System.out.println("Please enter a new file name:");
				Scanner scanFileName=new Scanner(System.in);
				//if the args[0] is not exist, change the args[0] into the file name that the user type in
				args[0] = scanFileName.nextLine();
			}
		
		}
	}

}
