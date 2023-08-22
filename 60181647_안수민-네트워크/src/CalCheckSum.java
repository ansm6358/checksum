import java.util.Scanner;

public class CalCheckSum {

	public static void main(String[] args) {
		sendData();
		receiveData(); // CRC 코드를 전달해주든지 static으로 올리든지 해야함

	}

	private static void sendData() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("CRC 코드 입력");
		// case 1 이진수로 입력을 받는 경우

		while (!scanner.hasNextInt()) {
			System.out.println("옳은 수치가 아닙니다. 다시 입력하세요.");
			scanner.next();
		}
		int CRCCodeInt = Integer.parseInt(scanner.next(), 2);
		String CRCCode = Integer.toBinaryString(CRCCodeInt);

		System.out.println("원데이터 입력");
		while (!scanner.hasNextInt()) {
			System.out.println("옳은 수치가 아닙니다. 다시 입력하세요.");
			scanner.next();
		}
		int dataInt = Integer.parseInt(scanner.next(), 2);
		String data = Integer.toBinaryString(dataInt);

//		//case 2 십진수로 입력을 받는 경우
//		System.out.println("CRC 코드 입력");
//		while(!scanner.hasNextInt()) {
//			System.out.println("옳은 수치가 아닙니다. 다시 입력하세요.");
//			scanner.next();
//		}
//		String CRCCode1 = Integer.toBinaryString(scanner.nextInt());
//		
//		System.out.println("원데이터 입력");
//		while(!scanner.hasNextInt()) {
//			System.out.println("옳은 수치가 아닙니다. 다시 입력하세요.");
//			scanner.next();
//		}
//		String data1 = Integer.toBinaryString(scanner.nextInt());

		int CRCLength = CRCCode.length();
		String tempData =data;
		for (int i = 1; i < CRCLength; i++) {
			tempData = tempData + "0";
		}
		System.out.println(data);
//		 _________________
//	123	| 12345
		data= data + calCRC(tempData, CRCCode);
		System.out.println("전송될 코드: "+data);
	}

	private static String calCRC(String tempData, String CRCCode) {
		int startPont = 0;
		String checksum = "";
		for (int endPoint = CRCCode.length(); endPoint <= tempData.length(); endPoint++) {
			String resultdata = calXOR(tempData.substring(startPont, endPoint), CRCCode, checksum);

			tempData = tempData.substring(0, startPont) + resultdata + tempData.substring(endPoint);
			startPont++;
			checksum = checksum + " ";
			if (endPoint == tempData.length()) {
				System.out.println(checksum + resultdata);
				System.out.println("체크섬: " + resultdata);

			}
		}
		System.out.println(checksum + "1234");
		//제일 앞 부분 뺴줘야함
		System.out.println(tempData.substring(tempData.length() - CRCCode.length(), tempData.length()));
		return tempData.substring(tempData.length() - CRCCode.length(), tempData.length());
	}

	private static String calXOR(String data, String CRCCode, String checksum) {

		String zero = "0000";
		int remainder = 0;

		if (data.substring(0, 1).equals("0")) {
			System.out.println(checksum + data);
			System.out.println(checksum + zero);
			remainder = Integer.parseInt(data, 2) ^ Integer.parseInt(zero, 2);

		} else if (data.substring(0, 1).equals("1")) {
			System.out.println(checksum + data);
			System.out.println(checksum + CRCCode);
			remainder = Integer.parseInt(data, 2) ^ Integer.parseInt(CRCCode, 2);
		} else {
			System.out.println("오류가 발생했습니다. 시스템을 종류합니다.");
			System.exit(1);
		}

		String remainderBinary = Integer.toBinaryString(remainder);

		while (remainderBinary.length() < CRCCode.length()) {
			remainderBinary = "0" + remainderBinary;
		}
		return remainderBinary;

	}

	private static void receiveData() {
		// TODO Auto-generated method stub

	}
}
