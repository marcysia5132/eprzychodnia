<?php 

include_once 'lekarze.php';

$lekarz = new Lekarz();
$daneLekarzy = $lekarz->getdoctors();

header('Content-Type: application/json');
echo json_encode($daneLekarzy);

?>