<?php
// Author: Anna Graduleva


require "dbLibrary.php";

// Encodes every row in result gotten from sqlQuery
function encodeResult($result){
	for ($pos = 0; $pos < sizeof($result); $pos++){
		$result[$pos] = encodeRow($result[$pos]);
	}

	return $result;
}

// Encodes a single row of pub info
function encodeRow($row){
	$row["name"] = utf8_encode($row["name"]);
	$row["address"] = utf8_encode($row["address"]);
	
	return $row;
}

// Returns a random pub from a list of pubs that the user has not yet visited
function getRandomPub($userEmail){
	$sqlStatement = "SELECT *
	                FROM pubs
	                WHERE id NOT IN
	                    (SELECT pub_id
	                    FROM pubs_visited
	                    WHERE user_email = '" . $userEmail . "'
	                    )
	                ";
	$result = getSqlResult($sqlStatement);
	$result = encodeResult($result);

	$resultSize = sizeof($result);
	$randomIndex = rand(0, $resultSize - 1);
	$randomRow = $result[$randomIndex];
	
	$pubId = $randomRow["id"];
	setCurrentPubId($pubId, $userEmail);
	echo json_encode($randomRow);
}

// Sets users current_pub_id in users table to be equal to the pub gotten
function setCurrentPubId($pubId, $userEmail){
	$sqlStatement = 	"UPDATE users
						SET `current_pub_id` = " . $pubId . "
						WHERE `email` = '" . $userEmail . "'";
	executeSql($sqlStatement);
}

header("Content-type:application/json");

// This is to make sure that the user has provided an email
if(isset($_GET["email"])){
	$userEmail = $_GET["email"];
	getRandomPub($userEmail);
}
else{
    // A simple JSON message
    echo '{"message" : "No email provided"}';
}
?>

