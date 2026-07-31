<?php
require __DIR__ . '/db.php';
$error = null;
$success = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';

    if (strlen($username) < 3 || strlen($password) < 4) {
        $error = 'Username or password too short.';
    } else {
        $pdo = get_db();
        $stmt = $pdo->prepare("INSERT INTO users (username, password, role) VALUES (?, ?, 'user')");
        try {
            $stmt->execute([$username, sha1($password)]);
            $success = 'Account created. You can log in now.';
        } catch (Exception $e) {
            $error = 'Username already taken.';
        }
    }
}
?>
<!DOCTYPE html>
<html>
<head><title>NoteVault - Register</title></head>
<body>
<h1>Register</h1>
<?php if ($error): ?><p style="color:red"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<?php if ($success): ?><p style="color:green"><?= htmlspecialchars($success) ?></p><?php endif; ?>
<form method="post">
    <label>Username: <input type="text" name="username"></label><br>
    <label>Password: <input type="password" name="password"></label><br>
    <button type="submit">Register</button>
</form>
<p><a href="login.php">Back to login</a></p>
</body>
</html>
