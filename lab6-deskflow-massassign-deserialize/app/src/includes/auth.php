<?php

function require_login() {
    if (!isset($_SESSION['user_id'])) {
        header('Location: /login.php');
        exit;
    }
}

function require_role($roles) {
    require_login();
    $roles = is_array($roles) ? $roles : [$roles];
    if (!in_array($_SESSION['role'], $roles, true)) {
        http_response_code(403);
        die('Forbidden.');
    }
}

function current_user_id() {
    return $_SESSION['user_id'] ?? null;
}
