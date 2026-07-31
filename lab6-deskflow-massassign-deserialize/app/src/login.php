<?php
session_start();
require __DIR__ . '/db.php';

if (isset($_SESSION['user_id'])) {
    header('Location: /dashboard.php');
    exit;
}

$error = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';
    $hash = sha1($password);

    $pdo = get_db();
    $stmt = $pdo->prepare('SELECT id, username, role FROM users WHERE username = ? AND password_hash = ?');
    $stmt->execute([$username, $hash]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user) {
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['username'] = $user['username'];
        $_SESSION['role'] = $user['role'];
        header('Location: /dashboard.php');
        exit;
    } else {
        $error = 'Invalid username or password.';
    }
}
?>
<?php require __DIR__ . '/includes/header.php'; ?>
<h1>Log In</h1>
<?php if ($error): ?><p class="error"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post">
    <label>Username: <input type="text" name="username"></label><br>
    <label>Password: <input type="password" name="password"></label><br>
    <button type="submit">Log In</button>
</form>
<p><a href="/register.php">Register a customer account</a></p>
<?php require __DIR__ . '/includes/footer.php'; ?>
