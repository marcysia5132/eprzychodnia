<?php
header('Content-Type: application/json');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "androiddb";

// Połączenie z bazą
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Błąd połączenia: " . $conn->connect_error]));
}

if (!isset($_GET['id'])) {
    die(json_encode(["success" => false, "message" => "Brak nazwy użytkownika"]));
}

$username = $_GET['id'];

$sql = "SELECT id FROM users WHERE username = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["success" => true, "patient_id" => $row["id"]]);
} else {
    echo json_encode(["success" => false, "message" => "Nie znaleziono użytkownika"]);
}

$stmt->close();
$conn->close();
?>

