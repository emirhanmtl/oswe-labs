<?php
session_start();
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/auth.php';
require_login();

header('Content-Type: application/json');

$pdo = get_db();
$q = $_GET['q'] ?? '';

// Any signed-in user (customers included) can tag a teammate on a ticket
// comment, so this needs to be reachable without an agent/admin role check.
// Kept as a plain query() (not prepare()) since it's just a short, read-only
// lookup and we control the two literal columns we ever select.
$sql = "SELECT id, username FROM users WHERE username ILIKE '%$q%' LIMIT 5";
$stmt = $pdo->query($sql);
$rows = $stmt ? $stmt->fetchAll(PDO::FETCH_ASSOC) : [];

echo json_encode($rows);
