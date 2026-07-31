<?php

function get_db() {
    static $pdo = null;
    if ($pdo === null) {
        $host = getenv('DB_HOST') ?: 'db';
        $name = getenv('DB_NAME') ?: 'deskflow';
        $user = getenv('DB_USER') ?: 'deskflow';
        $pass = getenv('DB_PASSWORD') ?: 'deskflow_pw';
        $dsn = "pgsql:host=$host;dbname=$name";
        $pdo = new PDO($dsn, $user, $pass, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_SILENT,
        ]);
    }
    return $pdo;
}
