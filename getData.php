<?php
$servername = "localhost";
$username = "codeman";
$password = "attacker123";
$dbname = "codeman";

$lat = $_GET ['lat'];
$lon = $_GET ['lon'];
$targetDistance = $_GET ['dist'];
if ($lat == null || $lon == null || $targetDistance == null) {
	echo "exit";
	exit ();
}

header ( "Content-Type: text/html; charset=UTF-8" );
session_start ();

$mysqli = new mysqli ( $servername, $username, $password, $dbname );
if (mysqli_connect_error ()) {
	exit ( 'Connect Error (' . mysqli_connect_errno () . ') ' . mysqli_connect_error () );
}

$mysqli->set_charset ( 'utf8' );
// 37.5852276 126.9913394 1.5
$sql = "select (6371*acos(cos(radians(" . $lat . "))*cos(radians(latitude))*cos(radians(longitude)-radians(" . $lon . "))+sin(radians(" . $lat . "))*sin(radians(latitude)))) AS distance, latitude, longitude, type, content FROM sns HAVING distance <= " . $targetDistance . " ORDER BY distance LIMIT 0,1000";
$res = $mysqli->query ( $sql );
$result = array ();
while ( $row = mysqli_fetch_array ( $res ) ) {
	array_push ( $result, array (
			'latitude' => $row [1],
			'longitude' => $row [2],
			'type' => $row[3],
			'contents' => urlencode($row[4]),
			'distance' => $row[0]
	) );
}

$output = json_encode ( array (
		"result" => $result 
) );

echo urldecode ( $output );

$mysqli->close ();
?>
