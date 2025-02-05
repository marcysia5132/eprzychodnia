<?php

include_once 'db-connect.php';

class Appointment {

    private $db;

    // Nazwa tabeli z danymi wizyt
    private $db_table = "appointments";

    public function __construct(){
        $this->db = new DbConnect();
    }

    // Metoda pobiera wszystkie rekordy wizyt
    public function getAppointments(){
        $query = "SELECT appointments.id, appointments.date, appointments.patient_id, appointments.doctor_id, users.username
                  FROM appointments
                  INNER JOIN users ON appointments.patient_id = users.id";

        $result = mysqli_query($this->db->getDb(), $query);

        $appointments = array();

        if(mysqli_num_rows($result) > 0){
            while($row = mysqli_fetch_assoc($result)){
                $appointments[] = $row;
            }
        }

        mysqli_close($this->db->getDb());
        return $appointments;
    }
}

$appointment = new Appointment();
$daneAppointments = $appointment->getAppointments();

header('Content-Type: application/json');
echo json_encode($daneAppointments);

?>

