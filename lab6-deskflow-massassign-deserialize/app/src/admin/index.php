<?php
require __DIR__ . '/guard.php';
require __DIR__ . '/../db.php';

$pdo = get_db();
$agents = $pdo->query("SELECT id, username, email, department FROM users WHERE role IN ('agent', 'admin') ORDER BY id")->fetchAll(PDO::FETCH_ASSOC);
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>Admin Panel</h1>
<p><a href="/admin/users.php">Manage users</a> | <a href="/admin/export.php">Export ticket audit report</a> | <a href="/admin/import.php">Import ticket audit report</a></p>

<h2>Staff accounts</h2>
<table border="1" cellpadding="6">
<tr><th>ID</th><th>Username</th><th>Email</th><th>Department</th></tr>
<?php foreach ($agents as $a): ?>
<tr>
    <td><?= (int)$a['id'] ?></td>
    <td><?= htmlspecialchars($a['username']) ?></td>
    <td><?= htmlspecialchars($a['email']) ?></td>
    <td><?= htmlspecialchars($a['department']) ?></td>
</tr>
<?php endforeach; ?>
</table>
<?php require __DIR__ . '/../includes/footer.php'; ?>
