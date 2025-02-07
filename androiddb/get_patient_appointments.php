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

if (!isset($_GET['patient_id'])) {
    die(json_encode(["success" => false, "message" => "Brak wymaganych danych"]));
}

$patient_id = $_GET['patient_id'];

$sql = "SELECT a.date, d.first_name, d.last_name, d.specialty, a.patient_id, a.doctor_id FROM appointments a JOIN doctors d ON a.doctor_id = d.id_doctor WHERE patient_id = ? ORDER BY date ASC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $patient_id);
$stmt->execute();
$result = $stmt->get_result();

$appointments = [];
while ($row = $result->fetch_assoc()) {
    $appointments[] = $row;
}

echo json_encode(["success" => true, "appointments" => $appointments]);

$stmt->close();
$conn->close();
?>
