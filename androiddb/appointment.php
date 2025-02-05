<?php

include_once 'db-connect.php';

class Appointment {
    private $db;
    private $db_table = "appointments";

    public function __construct(){
        $this->db = new DbConnect();
    }

    public function getAppointments($userId){
        // Znalezienie id_doctor na podstawie user_id
        $doctorQuery = "SELECT id_doctor FROM doctors WHERE user_id = ?";
        $stmt = mysqli_prepare($this->db->getDb(), $doctorQuery);
        mysqli_stmt_bind_param($stmt, "i", $userId);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        if ($row = mysqli_fetch_assoc($result)) {
            $doctorId = $row['id_doctor'];
        } else {
            mysqli_close($this->db->getDb());
            return [];
        }
        
        // Pobranie wizyt dla danego id_doctor
        $query = "SELECT appointments.id, appointments.date, appointments.patient_id, appointments.doctor_id, users.username
                  FROM appointments
                  LEFT JOIN users ON appointments.patient_id = users.id
                  WHERE doctor_id = ?";
        $stmt = mysqli_prepare($this->db->getDb(), $query);
        mysqli_stmt_bind_param($stmt, "i", $doctorId);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        $appointments = [];
        while ($row = mysqli_fetch_assoc($result)) {
            $appointments[] = $row;
        }

        mysqli_close($this->db->getDb());
        return $appointments;
    }
}

if (isset($_GET['user_id'])) {
    $userId = intval($_GET['user_id']);
    $appointment = new Appointment();
    $daneAppointments = $appointment->getAppointments($userId);
    header('Content-Type: application/json');
    echo json_encode($daneAppointments);
} else {
    echo json_encode(["error" => "Brak user_id w zapytaniu"]);
}

?>

