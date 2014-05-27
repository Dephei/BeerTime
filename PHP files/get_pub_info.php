<?php
// Author: Anna Graduleva

require "dbLibrary.php";

function startGetPubInfo($pubId){
    // Gets pub information according to the pub id
    $sqlStatement = "SELECT *
                    FROM pubs
                    WHERE id = " . $pubId . ";";
    $pubInfo = getSqlResult($sqlStatement);

    // If pub id does not exist in the database, then it getSqlResult will return empty array. If the array is empty, end the program
    if (sizeof($pubInfo) < 1){
        echo '{"message" : "pubId ' . $pubId . ' was not found in the database"}';
        return;
    }

    // All Swedish characters must be utf8 encoded, since json_encode returns null when it tries to encode Swedish characters
    $pubInfo[0]["name"] = utf8_encode($pubInfo[0]["name"]);
    $pubInfo[0]["address"] = utf8_encode($pubInfo[0]["address"]);

    echo json_encode($pubInfo);
}

header("Content-type:application/json");

// Checks if user has provided email.
if(isset($_GET["id"])){
    $pubId = $_GET["id"];
    startGetPubInfo($pubId);
}
else{
    echo '{"message" : "No id provided"}';
}

?>