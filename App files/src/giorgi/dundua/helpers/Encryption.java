package giorgi.dundua.helpers;

import java.math.BigInteger;

public class Encryption {
	boolean debug = false;
	
	public String encryptWord(String word){
		String encryptedWord = "";
		for (int pos = 0; pos < word.length(); pos++){
			encryptedWord += encryptCharacter(word.substring(pos, pos + 1));
		}
		return encryptedWord;
	}
	
	public String decryptWord(String word){
		String decryptedWord = "";
		for (int pos = 0; pos < word.length(); pos++){
			decryptedWord += decryptCharacter(word.substring(pos, pos + 1));
		}
		return decryptedWord;
	}

	private String encryptCharacter(String stringCharacter) {

		String binaryCharacter = stringToBinary(stringCharacter);
		String reversedBinary = new StringBuilder(binaryCharacter).reverse().toString();
		String hex = Long.toHexString(Long.parseLong(reversedBinary, 2));
		String reversedHex = new StringBuilder(hex).reverse().toString();
		String encryptedBinary = new BigInteger(reversedHex, 16).toString(2);
		String encryptedCharacter = binaryToString(encryptedBinary);

		if (debug) {
			System.out.println("1. ASCII: " + stringCharacter);
			System.out.println("2. String to binary: " + binaryCharacter);
			System.out.println("3. Binary reversed: " + reversedBinary);
			System.out.println("4. Binary to hex: " + hex);
			System.out.println("5. Hex reversed: " + reversedHex);
			System.out.println("6. Hex to binary: " + encryptedBinary);
			System.out.println("7. Binary to ASCII: " + encryptedCharacter);
			System.out.println("\n");
		}

		return encryptedCharacter;
	}

	private String decryptCharacter(String stringCharacter) {
		String binaryCharacter = stringToBinary(stringCharacter);
		String hex = Long.toHexString(Long.parseLong(binaryCharacter, 2));
		String reversedHex = new StringBuilder(hex).reverse().toString();
		String decryptedBinary = new BigInteger(reversedHex, 16).toString(2);
		while (decryptedBinary.length() < 8){
			decryptedBinary = "0" + decryptedBinary;
		}
		String reversedBinary = new StringBuilder(decryptedBinary).reverse().toString();
		String decryptedCharacter = binaryToString(reversedBinary);

		if (debug) {
		System.out.println("7. ASCII: " + stringCharacter);
		System.out.println("6. String to binary: " + binaryCharacter);
		System.out.println("5. Binary to hex: " + hex);
		System.out.println("4. Hex reversed: " + reversedHex);
		System.out.println("3. Hex to binary: " + decryptedBinary);
		System.out.println("2. Binary reversed: " + reversedBinary);
		System.out.println("1. Binary to ASCII: " + decryptedCharacter);
		}
		return decryptedCharacter;
	}

	private String stringToBinary(String stringCharacter) {
		
		char character = stringCharacter.charAt(0);
		String binaryCharacter = Integer.toBinaryString(character);

		while (binaryCharacter.length() < 8) {
			binaryCharacter = "0" + binaryCharacter;
		}

		return binaryCharacter;
	}

	private String binaryToString(String binary) {
		int characterCode = Integer.parseInt(binary, 2);
		String encryptedCharacter = new Character((char) characterCode).toString();

		return encryptedCharacter;
	}

}