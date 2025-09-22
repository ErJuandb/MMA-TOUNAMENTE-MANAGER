<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Habilitar logging de errores
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Configuración de la base de datos
$host = "localhost";
$db_name = "rankone";
$username = "root";
$password = "";

try {
    // Conexión a la base de datos
    $conn = new PDO("mysql:host=" . $host . ";dbname=" . $db_name . ";charset=utf8", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $conn->setAttribute(PDO::ATTR_TIMEOUT, 5); // 5 segundos de timeout
    
    // Consulta SQL
    $query = "SELECT id, nombre, arteMarcial, pais, localidad, 
                     fechaInicio, fechaFin, idOrganizador, ganador 
              FROM competiciones";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    
    // Obtener resultados
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Asegurarse de que todos los campos sean strings válidos
    foreach ($result as &$row) {
        foreach ($row as $key => &$value) {
            if ($value === null) {
                $value = "";
            }
        }
    }
    
    // Log para depuración
    error_log("Datos obtenidos: " . json_encode($result));
    
    echo json_encode(array(
        "status" => true,
        "message" => "Competiciones obtenidas correctamente",
        "data" => $result
    ), JSON_UNESCAPED_UNICODE);
    
} catch(PDOException $e) {
    error_log("Error de base de datos: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        "status" => false,
        "message" => "Error de conexión: " . $e->getMessage(),
        "data" => null
    ), JSON_UNESCAPED_UNICODE);
} catch(Exception $e) {
    error_log("Error general: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        "status" => false,
        "message" => "Error general: " . $e->getMessage(),
        "data" => null
    ), JSON_UNESCAPED_UNICODE);
}
?> 