<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);
$rawData = file_get_contents("php://input");
$_POST = json_decode($rawData, true);

$servername = "localhost";
$username = "root";  // Zmień na własne dane, jeśli masz inne
$password = "";
$dbname = "androiddb";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Błąd połączenia z bazą danych: " . $conn->connect_error]));
}

if (empty($_POST['doctor_id']) || empty($_POST['date'])) {
    die(json_encode(["success" => false, "message" => "Brak wymaganych danych: doctor_id lub date"]));
}

// NULL dla patient_id
$doctor_id = $_POST['doctor_id'];
$date = $_POST['date'];
$patient_id = isset($_POST['patient_id']) && $_POST['patient_id'] !== '' ? $_POST['patient_id'] : NULL;

// Sprawdzenie, czy wizyta istnieje
$check_sql = "SELECT patient_id FROM appointments WHERE doctor_id = ? AND date = ?";
$stmt = $conn->prepare($check_sql);
$stmt->bind_param("is", $doctor_id, $date);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    if (is_null($row["patient_id"])) {
        die(json_encode(["success" => false, "message" => "Wizyta już jest anulowana"]));
    }
}

// Aktualizacja patient_id na NULL w przypadku anulowania wizyty
$update_sql = "UPDATE appointments SET patient_id = NULL, info = NULL WHERE doctor_id = ? AND date = ?";
$stmt = $conn->prepare($update_sql);
$stmt->bind_param("is", $doctor_id, $date);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Wizyta została anulowana"]);
} else {
    echo json_encode(["success" => false, "message" => "Błąd SQL: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
