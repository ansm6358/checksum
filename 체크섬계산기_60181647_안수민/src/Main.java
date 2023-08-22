import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("CRC 코드를 입력해주세요.");

		String CRC = scanner.next();
		while (!checkBinary(CRC)) { // 이진수만 받기 위함
			System.out.println("옳은 수치가 아닙니다. 다시 입력하세요.");
			CRC = scanner.next();
		}
		int CRCCodeInt = Integer.parseInt(CRC, 2);
		String CRCCode = Integer.toBinaryString(CRCCodeInt); // int로 변환했다가 다시 변환하는 이유는 CRC 코드는 다항코드를 이진수로 표현한 것이기 때문에 맨앞을 1로하기 위함
																// 예시 010을 입력한 경우 0x^2+ 1x^1+0 이므로 crc 코드는 10이 되야함

		String data = sendData(CRCCode, scanner); // 데이터를 체크섬 계산해서 보내는 부분, data에는 데이터와 체크섬이 합쳐진 수치가 들어감
		receiveData(data, CRCCode); // 데이터를 받아서 체크섬 계산해서 확인하는 부분
		scanner.close();
	}

	private static boolean checkBinary(String data) {	//0과 1을 전부 ""으로 바꾼 뒤 크기를 해당데이터의 크기가 0보다 클 경우 0, 1이 아닌 다른 것을 입력한 상태 
		String tempData = data;
		tempData = tempData.replace("0", "");
		tempData = tempData.replace("1", "");

		if (tempData.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	private static String sendData(String CRCCode, Scanner scanner) {
		System.out.println("전송할 원데이터 입력");
		String data = scanner.next(); // 데이터 부분은 맨 앞이 0이 올수도 있다고 생각되서 위와 다르게 맨 앞에 0을 없애지 않음
		
		while (!checkBinary(data)) { // 위와 동일하게 이진수만 받기 위함
			System.out.println("옳은 수치가 아닙니다. 다시 입력하세요.");
			data = scanner.next();
		}

		int CRCLength = CRCCode.length();
		String tempData = data;
		for (int i = 1; i < CRCLength; i++) { // 체크섬 계산을 위해 기존 데이터에 crc 코드의 길이-1만큼 0을 붙임
			tempData = tempData + "0";
		}

		String checkSum = calCheckSum(tempData, CRCCode); // 체크섬 계산해서 리턴 받음
		data = data + checkSum; // 데이터에 체크섬 추가
		System.out.println("전송할 데이터: " + data);
		System.out.println();
		System.out.println();
		return data;

	}

	private static void receiveData(String data, String CRCCode) {
		System.out.println("받은 데이터: " + data);
		String checkSum = calCheckSum(data, CRCCode); // 체크섬 계산해서 리턴 받음
		System.out.println("체크섬 계산 결과: " + checkSum);

		if (Integer.parseInt(checkSum, 2) != 0) { // crc 코드를 공유하기 때문에 체크섬이 0이 나와야 하지만 아닌 경우 오류로 판단하고 종료
			System.out.println("체크섬 계산 결과 오류 발생, 프로그램을 종료합니다");
			System.exit(1);
		} else {
			System.out.println("정상적으로 데이터 받음");
		}

		String originData = data.substring(0, data.length() - CRCCode.length() + 1); // 받은 데이터에서 체크섬 부분 분리해서 원데이터를 받는다
		// 체크섬의 길이는 crc코드의 길이 -1 이므로 crc코드의 길이 -1만큼 빼서 계산은 -crc코드의 길이 +1이다
		System.out.println("원데이터: " + originData);
	}

	private static String calCheckSum(String tempData, String CRCCode) {
		int startPont = 0; // xor계산시 계산 할 부분만 분리하기 위해 사용 예)데이터가 1011이고 crc코드가 10일 때, 먼저 데이터 부분의 10만 떼서 계산(그 다음은 01만 떼서 계산)
		String checksum = ""; // 처음에는 xor계산을 프린트 할 떄 보기편하게 하는 용도로 쓰이다 마지막에 최종 체크섬 저장
		String resultdata = ""; // xor계산의 중간 계산값 임시저장
		String zero = ""; // 굳이 필요는 없는 부분이지만 0과의 xor 계산을 보여주기 위해 사용
		for (int i = 0; i < CRCCode.length(); i++) { // crc코드의 자릿수만큼 0을 붙임
			zero = zero + "0";
		}

		System.out.println(tempData);

		for (int endPoint = CRCCode.length(); endPoint <= tempData.length(); endPoint++) { // xor 계산되는 부분만 나눠서 계산

			if (tempData.substring(startPont, startPont + 1).equals("0")) { // 계산 할 부분의 제일 앞이 0인 경우 0과 xor 계산
				resultdata = calXOR(tempData.substring(startPont, endPoint), zero, checksum);

			} else if (tempData.substring(startPont, startPont + 1).equals("1")) { // 계산 할 부분의 제일 앞이 1인 경우 CRC 코드와 xor
																					// // 계산
				resultdata = calXOR(tempData.substring(startPont, endPoint), CRCCode, checksum);

			} else { // 만약 0과 1이 아닌 다른 것일 경우 오류이므로 시스템 종료
				System.out.println("오류가 발생했습니다. 시스템을 종류합니다.");
				System.exit(1);
			}

			tempData = tempData.substring(0, startPont) + resultdata + tempData.substring(endPoint); // xor계산 중간 과정 저장
			// 예)데이터가 1011이고 crc코드가 10일 때, 처음 계산 뒤 0011이 되고 01로 xor계산 진행

			startPont++; // 앞부분 계산 끝난 그 뒷자리 계산을 하기 위해 포인터를 하나 뒤로 옮김
			checksum = checksum + " "; // 시각적으로 보기편하게 하기 위함
		}

		System.out.println(checksum + tempData.substring(startPont, tempData.length()));
		checksum = tempData.substring(startPont, tempData.length()); // 체크섬 저장
		System.out.println("체크섬: " + checksum);
		return checksum;
	}

	private static String calXOR(String data, String CRCCode, String checksum) {
		int result = 0;

		System.out.println(checksum + data);
		System.out.println(checksum + CRCCode);
		result = Integer.parseInt(data, 2) ^ Integer.parseInt(CRCCode, 2); // String 타입을 int형으로 변환시켜 xor 계산

		String resultBinary = Integer.toBinaryString(result); // xor 계산 후 다시 String으로 변환
		while (resultBinary.length() < CRCCode.length()) { // 예 11과 10을 계산하면 01이 나오는데 문자형으로 변환하면 1만 나오니까 1앞에 사라진 0을 추가한다
			resultBinary = "0" + resultBinary;
		}
		return resultBinary;
	}

}
