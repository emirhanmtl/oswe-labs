<?php
session_start();
require __DIR__ . '/db.php';

$error = null;
$success = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';
    $email = $_POST['email'] ?? '';

    if (strlen($username) < 3 || strlen($password) < 6) {
        $error = 'Username or password too short.';
    } else {
        $pdo = get_db();
        $stmt = $pdo->prepare("INSERT INTO users (username, password_hash, role, email) VALUES (?, ?, 'customer', ?)");
        try {
            $stmt->execute([$username, sha1($password), $email]);
            $success = 'Account created. You can log in now.';
        } catch (Exception $e) {
            $error = 'Username already taken.';
        }
    }
}
?>
<?php require __DIR__ . '/includes/header.php'; ?>
<h1>Register</h1>
<?php if ($error): ?><p class="error"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<?php if ($success): ?><p class="success"><?= htmlspecialchars($success) ?></p><?php endif; ?>
<form method="post">
    <label>Username: <input type="text" name="username"></label><br>
    <label>Email: <input type="text" name="email"></label><br>
    <label>Password: <input type="password" name="password"></label><br>
    <button type="submit">Register</button>
</form>
<p><a href="/login.php">Back to login</a></p>
<?php require __DIR__ . '/includes/footer.php'; ?>
