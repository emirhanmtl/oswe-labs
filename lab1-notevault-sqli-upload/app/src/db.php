<?php

function get_db() {
    static $pdo = null;
    if ($pdo === null) {
        $host = getenv('DB_HOST') ?: 'db';
        $dsn = "mysql:host=$host;dbname=notevault;charset=utf8mb4";
        $pdo = new PDO($dsn, 'notevault', 'notevault_pw', [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_SILENT,
        ]);
    }
    return $pdo;
}
