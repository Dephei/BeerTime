<?php
// Author: Saimonas Sileikis

require "dbLibrary.php";

function startSetUserDestination($email, $pubId){
    $sqlStatement = 	"UPDATE users
						SET `current_pub_id` = " . $pubId . "
						WHERE `email` = '" . $email . "'";
    executeSql($sqlStatement);
    echo "User destination has been changed.";
}

// Checks if user has provided email.
if(isset($_GET["email"])){
    $email = $_GET["email"];
    if(isset($_GET["pubid"])){
        $pubId = $_GET["pubid"];
        startSetUserDestination($email, $pubId);
    }
    else{
        echo "pubid not set";
    }
}
else{
    echo "email not set";
}


?>