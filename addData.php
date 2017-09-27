<?php
$servername = "localhost";
$username = "codeman";
$password = "attacker123";
$dbname = "codeman";

$lat = $_GET ['lat'];
$lon = $_GET ['lon'];
$contents = $_GET ['contents'];
$type = $_GET[type];

if ($lat == null || $lon == null || $type == null) {
	echo "fail";
	exit ();
}
if( $contents == null )
	$contents = "";
header ( "Content-Type: text/html; charset=UTF-8" );
session_start ();

$mysqli = new mysqli ( $servername, $username, $password, $dbname );
if (mysqli_connect_error ()) {
	exit ( 'Connect Error (' . mysqli_connect_errno () . ') ' . mysqli_connect_error () );
}

$mysqli->set_charset ( 'utf8' );
// 37.5852276 126.9913394 1.5
$sql = "INSERT INTO `sns` (`latitude`, `longitude`, `type`, `content`) VALUES ('$lat', '$lon', '$type', '$contents')";

if ($mysqli->query($sql) === TRUE) {
    echo "success";
} else {
    echo "fail";
}

$mysqli->close ();

?>
