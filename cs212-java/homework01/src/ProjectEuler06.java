public class ProjectEuler06 {
	//function of sum of squares of numbers from 1 to max
	public static int sumSquares(int max) {
		int sum=0;
		for (int i = 1; i <= max; i++) {
			sum+=(i*i);
		}
		return sum;
	}
	//function of square of sum of numbers from 1 to max
	public static int squareSums(int max) {
		int sum=0;
		for (int i=1;i<=max;i++){
			sum+=i;
		}
		
		return sum*sum;
	}

	public static int solve(int max) {
		return squareSums(max)-sumSquares(max);
	}

	public static void main(String[] args) {

		// change as necessary to debug
		int n = 100;

		System.out.printf("sumOfSquares(%d) = %d\n", n, sumSquares(n));
		System.out.printf("squareOfSums(%d) = %d\n", n, squareSums(n));
		System.out.printf("solve(%d) = %d\n", n, solve(n));
	}
}