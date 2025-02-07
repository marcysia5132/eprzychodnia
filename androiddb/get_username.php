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
    die(json_encode(["success" => false, "message" => "Brak wymaganych danych"]));
}

$id = $_GET['id'];

$sql = "SELECT username FROM users WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["success" => true, "username" => $row["username"]]);
} else {
    echo json_encode(["success" => false, "message" => "Nie znaleziono użytkownika"]);
}

$stmt->close();
$conn->close();
?>
