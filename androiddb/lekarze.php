<?php 

include_once 'db-connect.php';

class Lekarz {
    
    private $db;
    
    // Nazwa tabeli z danymi lekarzy
    private $db_table = "doctors";
    
    public function __construct(){
        $this->db = new DbConnect();
    }
    
    // Metoda pobiera wszystkie rekordy lekarzy
    public function getdoctors(){
        $query = "SELECT id_doctor, first_name, last_name, specialty FROM " . $this->db_table;
        $result = mysqli_query($this->db->getDb(), $query);
        
        $doctors = array();
        
        if(mysqli_num_rows($result) > 0){
            while($row = mysqli_fetch_assoc($result)){
                $doctors[] = $row;
            }
        }
        
        mysqli_close($this->db->getDb());
        return $doctors;
    }
}
?>
