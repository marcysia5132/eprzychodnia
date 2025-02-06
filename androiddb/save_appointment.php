<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "localhost";
$username = "root";  // Zmień na własne dane, jeśli masz inne
$password = "";
$dbname = "androiddb";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Błąd połączenia z bazą danych: " . $conn->connect_error]));
}

if (!isset($_POST['doctor_id']) || !isset($_POST['patient_id']) || !isset($_POST['date'])) {
    die(json_encode(["success" => false, "message" => "Brak wymaganych danych"]));
}

$doctor_id = $_POST['doctor_id'];
$patient_id = $_POST['patient_id'];
$date = $_POST['date'];

// Sprawdzenie, czy wizyta już ma przypisanego pacjenta
$check_sql = "SELECT patient_id FROM appointments WHERE doctor_id = ? AND date = ?";
$stmt = $conn->prepare($check_sql);
$stmt->bind_param("is", $doctor_id, $date);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    if (!is_null($row["patient_id"])) {
        die(json_encode(["success" => false, "message" => "Ta wizyta jest już zajęta"]));
    }
}

// Aktualizacja `patient_id` na wybranego pacjenta
$update_sql = "UPDATE appointments SET patient_id = ? WHERE doctor_id = ? AND date = ?";
$stmt = $conn->prepare($update_sql);
$stmt->bind_param("iis", $patient_id, $doctor_id, $date);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Wizyta została zapisana"]);
} else {
    echo json_encode(["success" => false, "message" => "Błąd SQL: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
