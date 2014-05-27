<?php
require "dbLibrary.php";
// Author: Saimonas Sileikis

// Gets information about the user stored in the USERS table of the database
function getBasicUserInfo($userEmail){
    $sqlStatement = "SELECT *
                    FROM users
                    WHERE email = '" . $userEmail . "';";
    $sqlResult = getSqlResult($sqlStatement);

    $id = $sqlResult[0]["id"];
    $email = $sqlResult[0]["email"];
    $lastUpdate = $sqlResult[0]["last_update"];
    $lastLatitude = $sqlResult[0]["last_latitude"];
    $lastLongitude = $sqlResult[0]["last_longitude"];
    $currentPubId = $sqlResult[0]["current_pub_id"];
    $score = $sqlResult[0]["score"];

    $basicUserInfo = Array("id" => $id,
        "email" => $email,
        "lastUpdate" => $lastUpdate,
        "lastLatitude" => $lastLatitude,
        "lastLongitude" => $lastLongitude,
        "currentPubId" => $currentPubId,
        "score" => $score);
    return $basicUserInfo;

}

// Uses userEmail to get information about all the pubs visited by the user
function getVisitedPubs($userEmail){
    $sqlStatement = "SELECT *
                    FROM pubs
                    INNER JOIN
	                    (SELECT pub_id
	                    FROM pubs_visited
	                    WHERE user_email = '" . $userEmail . "') temp
                    ON pubs.id = temp.pub_id;";
    $sqlResult = getSqlResult($sqlStatement);

    $visitedPubs = encodeVisitedPubs($sqlResult);


    return $visitedPubs;
}

// json_encode function is unable to process Swedish characters. If it encounters any Swedish characters, it returns null
function encodeVisitedPubs($visitedPubs){
    for ($pos = 0; $pos < sizeof($visitedPubs); $pos++){
        $visitedPubs[$pos]["name"] = utf8_encode($visitedPubs[$pos]["name"]);
        $visitedPubs[$pos]["address"] = utf8_encode($visitedPubs[$pos]["address"]);
    }

    return $visitedPubs;
}

// The main function to call. Calling this, will return an associative array (dictionary) for all user values, including pubs visited
function getUserInfo($userEmail){
    $userInfo = Array("exists" => true);
    if (isNewUser($userEmail)){
        $userInfo["exists"] = false;
    }
    else {
        $userInfo += getBasicUserInfo($userEmail);
        $visitedPubs = getVisitedPubs($userEmail);
        $userInfo["visitedPubs"] = $visitedPubs;
    }
    return $userInfo;
}

?>