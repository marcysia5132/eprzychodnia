<?php
include_once 'db-connect.php';

header("Content-Type: application/json");
error_reporting(E_ALL);
ini_set('display_errors', 1);

class Appointment {
    private $db;
    private $db_table = "appointments";

    public function __construct(){
        $this->db = new DbConnect();
    }

    public function updateAppointmentInfo($appointmentId, $info) {
        $query = "UPDATE appointments SET info = ? WHERE id = ?";
        $stmt = mysqli_prepare($this->db->getDb(), $query);
        
        if (!$stmt) {
            return ["status" => "error", "message" => "Błąd przygotowania zapytania"];
        }

        mysqli_stmt_bind_param($stmt, "si", $info, $appointmentId);

        if (mysqli_stmt_execute($stmt)) {
            return ["status" => "success", "message" => "Info updated successfully"];
        } else {
            return ["status" => "error", "message" => "Błąd wykonania zapytania"];
        }
    }
}

// Odbieranie danych z POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);

    file_put_contents('log.txt', json_encode($data) . PHP_EOL, FILE_APPEND); // Logowanie zapytania

    if (isset($data['appointment_id']) && isset($data['info'])) {
        $appointmentId = $data['appointment_id'];
        $info = $data['info'];

        $appointment = new Appointment();
        $result = $appointment->updateAppointmentInfo($appointmentId, $info);

        echo json_encode($result);
    } else {
        echo json_encode(["status" => "error", "message" => "Brak wymaganych danych"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Nieprawidłowa metoda żądania"]);
}
?>