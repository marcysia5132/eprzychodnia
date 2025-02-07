<?php
    
    include_once 'db-connect.php';
    
    class User{
        
        private $db;
        
        private $db_table = "users";
        
        public function __construct(){
            $this->db = new DbConnect();
        }
        
        public function getUserData($username, $password){
            $query = "SELECT id, username, rola_id FROM ".$this->db_table." WHERE username = '$username' AND password = '$password' LIMIT 1";
            $result = mysqli_query($this->db->getDb(), $query);
        
            if(mysqli_num_rows($result) > 0){
                $row = mysqli_fetch_assoc($result);
                mysqli_close($this->db->getDb());
                return $row; // Zwracamy dane użytkownika
            }
        
            mysqli_close($this->db->getDb());
            return null; // Brak użytkownika
        }
        
        public function isEmailUsernameExist($username, $email){
            
            $query = "select * from ".$this->db_table." where username = '$username' OR email = '$email'";
            
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) > 0){
                
                mysqli_close($this->db->getDb());
                
                return true;
                
            }
            
            
            return false;
            
        }
        
        public function isValidEmail($email){
            return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
        }
        
        
        
        public function createNewRegisterUser($username, $password, $email){
            
            
            $isExisting = $this->isEmailUsernameExist($username, $email);
            
            
            if($isExisting){
                
                $json['success'] = 0;
                $json['message'] = "Błąd. Taka nazwa użytkownika lub adres e-mail już istnieją";
            }
            
            else{
                
            $isValid = $this->isValidEmail($email);
                
                if($isValid)
                {
                $query = "insert into ".$this->db_table." (username, password, email, created_at, updated_at) values ('$username', '$password', '$email', NOW(), NOW())";
                
                $inserted = mysqli_query($this->db->getDb(), $query);
                
                if($inserted == 1){
                    
                    $json['success'] = 1;
                    $json['message'] = "Zarejestrowano użytkownika";
                    
                }else{
                    
                    $json['success'] = 0;
                    $json['message'] = "Błąd. Taka nazwa użytkownika lub adres e-mail już istnieją";
                    
                }
                
                mysqli_close($this->db->getDb());
                }
                else{
                    $json['success'] = 0;
                    $json['message'] = "Błąd. Taki adres e-mail nie istnieje";

                
                }
                
            }
            
            return $json;
            
        }
        
        public function loginUsers($username, $password){
            $json = array();
            $userData = $this->getUserData($username, $password);
        
            if($userData){
                $json['success'] = 1;
                $json['message'] = "Zalogowano pomyślnie";
                $json['session'] = session_id(); 
                $json['rola_id'] = $userData['rola_id'];
                $json['id'] = $userData['id'];
            }else{
                $json['success'] = 0;
                $json['message'] = "Błędne dane";
            }
            return $json;
        }
        
        public function queryDatabase($query){
            
            $result = mysqli_query($this->db->getDb(), $query);  
            $myArray = array();
            while(!is_bool($result) && $row = $result->fetch_assoc()) {
                $myArray[] = $row;
            }
            
            mysqli_close($this->db->getDb());
            
            return $myArray;
            
        }
    }
    ?>
