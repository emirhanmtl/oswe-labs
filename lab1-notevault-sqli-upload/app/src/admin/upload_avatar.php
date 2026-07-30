<?php
require __DIR__ . '/guard.php';

$uploadDir = __DIR__ . '/../uploads/';
$blacklist = ['php', 'php3', 'php4', 'php5', 'phar', 'pht'];

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_FILES['avatar'])) {
    $file = $_FILES['avatar'];
    $originalName = basename($file['name']);
    $ext = strtolower(pathinfo($originalName, PATHINFO_EXTENSION));

    if (in_array($ext, $blacklist, true)) {
        die('File type not allowed.');
    }

    // Keep the original filename so users can recognize their own avatar.
    $destination = $uploadDir . $originalName;
    if (move_uploaded_file($file['tmp_name'], $destination)) {
        echo 'Upload successful. <a href="index.php">Back</a>';
    } else {
        echo 'Upload failed. <a href="index.php">Back</a>';
    }
} else {
    header('Location: index.php');
}
