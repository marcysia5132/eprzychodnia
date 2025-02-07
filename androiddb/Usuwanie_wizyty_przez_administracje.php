<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$rawData = file_get_contents("php://input");
$_POST = json_decode($rawData, true);

$servername = "localhost";
$username = "root";  
$password = "";
$dbname = "androiddb";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Błąd połączenia z bazą danych: " . $conn->connect_error]));
}

if (empty($_POST['doctor_id']) || empty($_POST['date'])) {
    die(json_encode(["success" => false, "message" => "Brak wymaganych danych: doctor_id lub date"]));
}

$doctor_id = $_POST['doctor_id'];
$date = $_POST['date'];

// Sprawdzenie, czy wizyta istnieje
$check_sql = "SELECT * FROM appointments WHERE doctor_id = ? AND date = ?";
$stmt = $conn->prepare($check_sql);
$stmt->bind_param("is", $doctor_id, $date);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    die(json_encode(["success" => false, "message" => "Wizyta nie istnieje"]));
}

// Usunięcie wizyty
$delete_sql = "DELETE FROM appointments WHERE doctor_id = ? AND date = ?";
$stmt = $conn->prepare($delete_sql);
$stmt->bind_param("is", $doctor_id, $date);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Wizyta została usunięta"]);
} else {
    echo json_encode(["success" => false, "message" => "Błąd SQL: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
