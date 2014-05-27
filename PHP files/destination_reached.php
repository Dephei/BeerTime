<?php
// Author: Saimonas Sileikis

require "userInfoLibrary.php";

// Gets the pub's location, according to pub's id
function getPubPos($pubId){

    // Gets the pub's location from the database, according to pub id
    $sqlStatement = "SELECT latitude, longitude
                    FROM pubs
                    WHERE id = " . $pubId;
    $sqlResult = getSqlResult($sqlStatement);

    $pubLatitude = $sqlResult[0]["latitude"];
    $pubLongitude = $sqlResult[0]["longitude"];

    return Array($pubLatitude, $pubLongitude);
}

// Uses Pythagoras formula to find distance between two points
function getDistance($coordsA, $coordsB){

    $differenceInX = $coordsA[1] - $coordsB[1];
    $differenceInY = $coordsA[0] - $coordsB[0];

    $sumOfDifferences = pow($differenceInX, 2) + pow($differenceInY, 2);
    $distance = sqrt($sumOfDifferences);

    return $distance;
}

// Increase the score of the user, by the amount of scoreBonus
function setScore($userInfo, $scoreBonus){
    $userEmail = $userInfo["email"];
    $userScore = $userInfo["score"];
    $userScore += $scoreBonus;

    $sqlStatement = "UPDATE users
                    SET score = " . $userScore . "
                    WHERE email = '" . $userEmail . "';";
    executeSql($sqlStatement);
}

function updatePubsVisited($userInfo){
    $userEmail = $userInfo["email"];
    $pubId = $userInfo["currentPubId"];

    $sqlStatement = "INSERT INTO pubs_visited(user_email, pub_id, date)
                    VALUES ('" . $userEmail . "', " . $pubId . ", NOW());";
    executeSql($sqlStatement);
}

function setUserCurrentPubIdToNull($userEmail){
    $sqlStatement = "UPDATE users
                    SET current_pub_id = null;";
    executeSql($sqlStatement);
}

// The main function
function startDestinationReached($userEmail){
    $userInfo = getUserInfo($userEmail);

    // Makes sure that the user exists
    if (!$userInfo["exists"]){
        echo '{"message" : "email: ' . $userEmail . ' does not exist"}';
        return;
    }

    // Makes sure that the user has a pub that he has to visit
    if ($userInfo["currentPubId"] == null){
        // A simple JSON message
        echo '{"message" : "The user has no current goal"}';
        return;
    }

    $userPos = Array($userInfo["lastLatitude"], $userInfo["lastLongitude"]);
    $pubPos = getPubPos($userInfo["currentPubId"]);
    $distance = getDistance($userPos, $pubPos);


    // Checks if the distance is less than ~50 meters
    if ($distance < 1110.0015){
        $userBonusScore = 10;
        updatePubsVisited($userInfo);
        setUserCurrentPubIdToNull($userEmail);
        setScore($userInfo, $userBonusScore);
        // A simple JSON message
        echo '{"destinationReached : "true", "distance" : "' . $distance . '", "pointsAwarded = ' . $userBonusScore . '}';
    }
    else{
        // A simple JSON message
        echo '{"destinationReached : "false", "distance" : ' . $distance . ', "pointsAwarded = 0}';
    }
}

header("Content-type:application/json");

if(isset($_GET["email"])){
    $userEmail = $_GET["email"];
    startDestinationReached($userEmail);
}
else{
    echo '{"message" : "No email provided"}';
}

?>