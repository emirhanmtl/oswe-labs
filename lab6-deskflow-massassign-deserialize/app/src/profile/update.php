<?php
session_start();
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/auth.php';
require_login();

$pdo = get_db();
$success = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // "Flexible" profile editor: whatever fields the form sends get written
    // straight through, so adding a new self-service field later (title,
    // timezone, ...) never needs a backend change.
    $fields = [];
    $values = [];
    foreach ($_POST as $key => $value) {
        if ($key === 'csrf_token' || $value === '') {
            continue;
        }
        $fields[] = $key . ' = ?';
        $values[] = $value;
    }

    if ($fields) {
        $sql = 'UPDATE users SET ' . implode(', ', $fields) . ' WHERE id = ?';
        $values[] = current_user_id();
        $stmt = $pdo->prepare($sql);
        $stmt->execute($values);
        $success = 'Profile updated.';

        // Role can change as a result of this update (e.g. a promotion),
        // so keep the session in sync with the DB.
        $stmt = $pdo->prepare('SELECT role FROM users WHERE id = ?');
        $stmt->execute([current_user_id()]);
        $_SESSION['role'] = $stmt->fetchColumn();
    }
}

$stmt = $pdo->prepare('SELECT email, department FROM users WHERE id = ?');
$stmt->execute([current_user_id()]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>My Profile</h1>
<?php if ($success): ?><p class="success"><?= htmlspecialchars($success) ?></p><?php endif; ?>
<form method="post">
    <label>Email: <input type="text" name="email" value="<?= htmlspecialchars($user['email'] ?? '') ?>"></label><br>
    <label>Department: <input type="text" name="department" value="<?= htmlspecialchars($user['department'] ?? '') ?>"></label><br>
    <button type="submit">Save</button>
</form>
<?php require __DIR__ . '/../includes/footer.php'; ?>
