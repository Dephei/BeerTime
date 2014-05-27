<?php
// Author: Anna Graduleva

require "userInfoLibrary.php";

header("Content-type:application/json");

// Checks if user has provided email.
if(isset($_GET["email"])){
    $userEmail = $_GET["email"];
    $userInfo = getUserInfo($userEmail);

    echo json_encode($userInfo);
}
else{
    echo '{"message" : "No email provided"}';
}


?>