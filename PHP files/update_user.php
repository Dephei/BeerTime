<?php
// Author: Anna Graduleva

require "dbLibrary.php";

// Creates new user, according to email, latitude and longitude
function createNewUser($userEmail, $userLatitude, $userLongitude){
	$sqlStatement = 	"INSERT INTO users(email, last_update, last_latitude, last_longitude)
						VALUES ('" .  $userEmail . "', NOW(), " .  $userLatitude . ", " .  $userLongitude . ");";
	executeSql($sqlStatement);
	
}

// Updates the position of an existing user
function updateExistingUser($userEmail, $userLatitude, $userLongitude){
	$sqlStatement = 	"UPDATE users
						SET `last_update` = NOW(), `last_latitude` = " .  $userLatitude . ", `last_longitude` = " .  $userLongitude . "
						WHERE `email` = '" . $userEmail . "'";
	executeSql($sqlStatement);
	
}

// The main function
function starUpdate_user($userEmail, $userLatitude, $userLongitude){

    // Makes sure that the user exists
	if(isNewUser($userEmail)){
        // A simple JSON message
		echo '{"message" : "Email ' .  $userEmail . ' not found in the database. Creating new user with Latitude: ' . $userLatitude . ' and Longitude: ' . $userLongitude . '"}';
		createNewUser($userEmail, $userLatitude, $userLongitude);
	}
	else{
        // A simple JSON message
        echo '{"message" : "Updating ' . $userEmail . ' with Latitude: ' . $userLatitude . ' and Longitude: ' . $userLongitude . '"}';
		updateExistingUser($userEmail, $userLatitude, $userLongitude);
	}
}

header("Content-type:application/json");

if (isset($_GET['email']) and isset($_GET['latitude']) and isset($_GET['longitude'])) {
    $userEmail = $_GET['email'];
	$userLatitude = $_GET['latitude'];
	$userLongitude = $_GET['longitude'];
	
	starUpdate_user($userEmail, $userLatitude, $userLongitude);
}
else{
    // A simple JSON message
	echo '{"message" : "You are missing parameters. Make sure that you have email, latitude and longitude parameters. Example: http://83.248.10.39/JSON/update_user.php?email=test@email.com&latitude=16.1245&longitude=27.8738"}';
}

?>