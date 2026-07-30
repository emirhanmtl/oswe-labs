<?php
session_start();
require __DIR__ . '/db.php';

$error = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';
    $password_hash = sha1($password);

    $pdo = get_db();

    // Legacy query built by string concatenation - kept from the old PHP4 codebase.
    $sql = "SELECT id, username, role FROM users WHERE username = '$username' AND password = '$password_hash' LIMIT 1";
    $stmt = $pdo->query($sql);
    $user = $stmt ? $stmt->fetch(PDO::FETCH_ASSOC) : false;

    if ($user) {
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['username'] = $user['username'];
        $_SESSION['role'] = $user['role'];
        header('Location: notes.php');
        exit;
    } else {
        $error = 'Invalid username or password.';
    }
}
?>
<!DOCTYPE html>
<html>
<head><title>NoteVault - Login</title></head>
<body>
<h1>NoteVault</h1>
<?php if ($error): ?><p style="color:red"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post">
    <label>Username: <input type="text" name="username"></label><br>
    <label>Password: <input type="password" name="password"></label><br>
    <button type="submit">Login</button>
</form>
<p><a href="register.php">Register</a></p>
</body>
</html>
