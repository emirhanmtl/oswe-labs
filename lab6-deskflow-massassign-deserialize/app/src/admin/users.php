<?php
require __DIR__ . '/guard.php';
require __DIR__ . '/../db.php';

$pdo = get_db();
$users = $pdo->query('SELECT id, username, role, email, department FROM users ORDER BY id')->fetchAll(PDO::FETCH_ASSOC);
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>All Users</h1>
<table border="1" cellpadding="6">
<tr><th>ID</th><th>Username</th><th>Role</th><th>Email</th><th>Department</th></tr>
<?php foreach ($users as $u): ?>
<tr>
    <td><?= (int)$u['id'] ?></td>
    <td><?= htmlspecialchars($u['username']) ?></td>
    <td><?= htmlspecialchars($u['role']) ?></td>
    <td><?= htmlspecialchars($u['email']) ?></td>
    <td><?= htmlspecialchars($u['department']) ?></td>
</tr>
<?php endforeach; ?>
</table>
<?php require __DIR__ . '/../includes/footer.php'; ?>
