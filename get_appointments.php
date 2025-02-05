<?php
header('Content-Type: application/json');

// Ustawienia połączenia z bazą danych
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "androiddb";

// Stworzenie połączenia
$conn = new mysqli($servername, $username, $password, $dbname);

// Sprawdzenie połączenia
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Pobierz parametry z zapytania
$doctor_id = $_GET['doctor_id'];
$selected_date = $_GET['selected_date']; // format YYYY-MM-DD

// Zapytanie SQL z konwersją daty na odpowiedni format
$sql = "
    SELECT date, patient_id
    FROM appointments
    WHERE doctor_id = ? 
    AND DATE(date) = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("is", $doctor_id, $selected_date);
$stmt->execute();
$result = $stmt->get_result();

// Tworzenie tablicy wyników
$appointments = array();
while ($row = $result->fetch_assoc()) {
    // Zwróć pełną datę (z godziną)
    $appointments[] = $row;
}

// Zwrócenie wyników jako JSON
echo json_encode($appointments);

// Zamknięcie połączenia
$stmt->close();
$conn->close();
?>
