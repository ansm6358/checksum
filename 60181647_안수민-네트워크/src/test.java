import java.util.Scanner;

public class test {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String a = scanner.next();
		
		a= a.replace("0", "");
		a=a.replace("1", "");
		
		System.out.println(a+a.length());
	}

}
