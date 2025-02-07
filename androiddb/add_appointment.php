<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Dane połączenia z bazą danych
$servername = "localhost";
$dbusername = "root";    // zmień, jeśli używasz innych danych
$dbpassword = "";
$dbname = "androiddb";

// Nawiązanie połączenia z bazą danych
$conn = new mysqli($servername, $dbusername, $dbpassword, $dbname);
if ($conn->connect_error) {
    die(json_encode([
        "success" => false,
        "message" => "Błąd połączenia z bazą danych: " . $conn->connect_error
    ]));
}

// Sprawdzamy, czy metoda POST została użyta
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Sprawdzenie, czy przesłano wymagane dane
    if (empty($_POST['date']) || empty($_POST['time']) || empty($_POST['doctor_id'])) {
        die(json_encode([
            "success" => false,
            "message" => "Brak wymaganych danych: date, time lub doctor_id"
        ]));
    }
    
    $date = $_POST['date'];       // np. "2025-02-06"
    $time = $_POST['time'];       // np. "14:30:00"
    $doctor_id = $_POST['doctor_id'];
    
    // Łączymy datę i czas w jeden ciąg (przyjmując, że między nimi ma być spacja)
    $datetime = "$date $time"; // wynik: "2025-02-06 14:30:00"
    
    // Sprawdzamy, czy wizyta dla podanych parametrów już istnieje
    $check_stmt = $conn->prepare("SELECT id FROM appointments WHERE doctor_id = ? AND date = ?");
    if (!$check_stmt) {
        die(json_encode([
            "success" => false,
            "message" => "Błąd przygotowania zapytania (check): " . $conn->error
        ]));
    }
    $check_stmt->bind_param("is", $doctor_id, $datetime);
    $check_stmt->execute();
    $check_result = $check_stmt->get_result();
    if ($check_result->num_rows > 0) {
        echo json_encode([
            "success" => false,
            "message" => "wizyta o podanych parametrach już istnieje!"
        ]);
        $check_stmt->close();
        $conn->close();
        exit();
    }
    $check_stmt->close();
    
    // Przygotowanie zapytania przy użyciu prepared statement do wstawienia nowej wizyty
    $stmt = $conn->prepare("INSERT INTO appointments (date, patient_id, doctor_id) VALUES (?, NULL, ?)");
    if (!$stmt) {
        die(json_encode([
            "success" => false,
            "message" => "Błąd przygotowania zapytania: " . $conn->error
        ]));
    }
    
    // Bindowanie parametrów: "s" dla string (datetime) i "i" dla integer (doctor_id)
    $stmt->bind_param("si", $datetime, $doctor_id);
    
    // Wykonanie zapytania i zwrócenie odpowiedzi JSON
    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Wizyta została dodana"]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Błąd SQL: " . $stmt->error
        ]);
    }
    
    $stmt->close();
    $conn->close();
}
?>
