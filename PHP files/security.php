<?php
// Author: Saimonas Sileikis

function encryptWord($word, $showMoreInformation)
{
    $encryptedWord = "";
    for ($pos = 0; $pos < strlen($word); $pos++) {
        $character = substr($word, $pos, 1);
        $encryptedCharacter = encryptCharacter($character, $showMoreInformation);
        $encryptedWord = $encryptedWord . $encryptedCharacter;
    }
    return $encryptedWord;
}

function encryptCharacter($character, $showMoreInformation)
{
    // Convert the character to binary
    $binary = decbin(ord($character));

    // Makes sures that no zeroes are truncated at the beginning
    while (strlen($binary) < 7) {
        $binary = "0" . $binary;
    }

    // Reverse the order of binary
    $reversedBinary = strrev($binary);

    // Convert the binary to hex
    $hex = base_convert($reversedBinary, 2, 16);

    // Reverse the order of hex
    $reversedHex = strrev($hex);

    // Convert to hex to binary
    $encryptedBinary = 0 . base_convert($reversedHex, 16, 2);

    // Convert binary to string
    $encryptedCharacter = pack('H*', base_convert($encryptedBinary, 2, 16));


    if ($showMoreInformation) {
        echo "Encryption:" . "<br>";
        echo "1. Text: " . $character . "<br>";
        echo "2. Text to binary: " . $binary . "<br>";
        echo "3. Binary reversed: " . $reversedBinary . "<br>";
        echo "4. Binary to hex: " . $hex . "<br>";
        echo "5. Hex reverse: " . $reversedHex . "<br>";
        echo "6. Hex to binary: " . $encryptedBinary . "<br>";
        echo "7. Binary to text: " . $encryptedCharacter . "<br>";
        echo "<br>";
    }

    return $encryptedCharacter;
}

function decryptWord($word, $showMoreInformation)
{
    $decryptedWord = "";
    for ($pos = 0; $pos < strlen($word); $pos++) {
        $character = substr($word, $pos, 1);
        $decryptedCharacter = decryptCharacter($character, $showMoreInformation);
        $decryptedWord = $decryptedWord . $decryptedCharacter;
    }
    return $decryptedWord;
}

function decryptCharacter($character, $showMoreInformation)
{

    // Convert string to binary
    $binary = $binary = decbin(ord($character));

    // Convert binary to hex
    $hex = base_convert($binary, 2, 16);

    // Reverse the order of hex
    $reversedHex = strrev($hex);

    // Convert hex to binary
    $decryptedBinary = base_convert($reversedHex, 16, 2);

    // Reverse the order of binary
    $reversedBinary = strrev($decryptedBinary);

    // Makes sures that no zeroes are truncated at the end
    while (strlen($reversedBinary) < 7) {
        $reversedBinary = $reversedBinary . "0";
    }

    // Convert binary to ASCII
    $decryptedCharacter = pack('H*', base_convert($reversedBinary, 2, 16));


    if ($showMoreInformation) {
        echo "Decryption:" . "<br>";
        echo "7. Character: " . $character . "<br>";
        echo "6. Character to binary: " . $binary . "<br>";
        echo "5. Binary to hex: " . $hex . "<br>";
        echo "4. Hex reverse: " . $reversedHex . "<br>";
        echo "3. Hex to binary: " . $decryptedBinary . "<br>";
        echo "2. Word to binary: " . $reversedBinary . "<br>";
        echo "1. Binary to ASCII: " . $decryptedCharacter . "<br>";
        echo "<br>" . "<br>";
    }

    return $decryptedCharacter;
}


if (isset($_GET["word"])) {

    $word = $_GET["word"];
    if (strlen($word) == 1) {
        $showMoreInformation = true;
    } else {
        $showMoreInformation = false;
    }
    $encryptedWord = encryptWord($word, $showMoreInformation);
    $decryptedWord = decryptWord($encryptedWord, $showMoreInformation);

    echo "Encrypted word: " . $encryptedWord . "<br>";
    echo "Decrypted word: " . $decryptedWord . "<br>";


} else {
    echo "You need to provide a word to encrypt. Like this<br>";
    echo "http://localhost/JSON/security.php?word=TEXTHERE";

}











?>