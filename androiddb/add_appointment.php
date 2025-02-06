<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'db_connect.php'; // Plik z połączeniem do bazy danych

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Odbieramy dane POST
    $date = $_POST['date'];    // np. "2025-02-06"
    $time = $_POST['time'];    // np. "14:30:00"
    $doctor_id = $_POST['doctor_id'];

    // Łączymy datę i czas w jeden ciąg
    $datetime = "$date $time";

    // Wykonujemy zapytanie, wstawiając do kolumny 'date' połączoną wartość
    $query = "INSERT INTO appointments (date, doctor_id) VALUES ('$datetime', '$doctor_id')";

    if (mysqli_query($conn, $query)) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => mysqli_error($conn)]);
    }

    mysqli_close($conn);
}
?>
