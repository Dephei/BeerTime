<?php
// Author: Saimonas Sileikis

// Executes the provided SQL statement, but does not return anything
function executeSql($sqlStatement){
	$dbLink = getDbLink();
	mysql_query($sqlStatement, $dbLink);
}

// Executes the provided SQL statement and returns the result
function getSqlResult($sqlStatement){
	$dbLink = getDbLink();
	$dbQuery = mysql_query($sqlStatement, $dbLink);
	
	$results = array();	
	while ($row = mysql_fetch_assoc($dbQuery)) {
	$results[] = $row;
	}
	
	return $results;
}

// Connects to the test_database and returns the database link
function getDbLink(){
	//Database connection information
	$dbHost = "localhost";
	$dbUsername = "root";
	$dbPass = "";
	$dbName = "test_database";

	$dbLink = @mysql_connect("$dbHost", "$dbUsername", "$dbPass") or die ("Could not connect to MySQL");
	@mysql_select_db("$dbName") or die ("No database");	
	
	return $dbLink;
}

// Used for debugging/developent to easily format a result from a SQL query and use it to print it with echo
function formatResult($result){
	$formattedRow = "";
	foreach($result as $row){
		$formattedRow = $formattedRow . formatRow($row) .  "<br>";
	}
	return $formattedRow;
}

// Used for debugging/developent to easily format a row from a SQL query and use it to print it with echo
function formatRow($row){
	$formattedRow = "";
	foreach($row as $item){
		$formattedRow = $formattedRow .  $item . " ";
	}
	return $formattedRow;
}

// Returns true if provided email does not exists, else returns false
function isNewUser($userEmail){
    $sqlStatement = "SELECT *
                    FROM users
                    WHERE email = '" .  $userEmail . "';";
    $result = getSqlResult($sqlStatement);

    $resultSize = sizeof($result);
    if ($resultSize > 0){
        return false;
    }
    else{
        return true;
    }
}

?>