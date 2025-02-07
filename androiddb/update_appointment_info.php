<?php
header("Content-Type: application/json; charset=UTF-8"); // Nagłówek JSON
include_once 'db-connect.php';

class Appointment {
    private $db;
    private $db_table = "appointments";

    public function __construct(){
        $this->db = new DbConnect();
    }

    public function updateAppointmentInfo($appointmentId, $info) {
        $query = "UPDATE appointments SET info = ? WHERE id = ?";
        $stmt = mysqli_prepare($this->db->getDb(), $query);
        mysqli_stmt_bind_param($stmt, "si", $info, $appointmentId);

        if (mysqli_stmt_execute($stmt)) {
            return ["status" => "success", "message" => "Info updated successfully"];
        } else {
            return ["status" => "error", "message" => "Database error: " . mysqli_error($this->db->getDb())];
        }
    }
}

// **🔹 Obsługa POST**
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);

    if (isset($data['appointment_id']) && isset($data['info'])) {
        $appointmentId = $data['appointment_id'];
        $info = $data['info'];

        $appointment = new Appointment();
        $response = $appointment->updateAppointmentInfo($appointmentId, $info);
        echo json_encode($response);
    } else {
        echo json_encode(["status" => "error", "message" => "Missing data"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
}
?>
