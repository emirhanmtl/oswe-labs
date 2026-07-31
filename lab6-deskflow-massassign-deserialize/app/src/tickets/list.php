<?php
session_start();
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/auth.php';
require_login();

$pdo = get_db();

if ($_SESSION['role'] === 'customer') {
    $stmt = $pdo->prepare('SELECT t.id, t.subject, t.status, t.created_at FROM tickets t WHERE t.customer_id = ? ORDER BY t.created_at DESC');
    $stmt->execute([current_user_id()]);
} else {
    // Agents and admins can see every ticket in the queue.
    $stmt = $pdo->query('SELECT t.id, t.subject, t.status, t.created_at, u.username AS customer FROM tickets t JOIN users u ON u.id = t.customer_id ORDER BY t.created_at DESC');
}
$tickets = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>Tickets</h1>
<?php if ($_SESSION['role'] !== 'customer'): ?>
<p><a href="/tickets/search.php">Search all tickets</a></p>
<?php endif; ?>
<table border="1" cellpadding="6">
<tr><th>ID</th><th>Subject</th><th>Status</th><?php if ($_SESSION['role'] !== 'customer'): ?><th>Customer</th><?php endif; ?></tr>
<?php foreach ($tickets as $t): ?>
<tr>
    <td><a href="/tickets/view.php?id=<?= (int)$t['id'] ?>"><?= (int)$t['id'] ?></a></td>
    <td><?= htmlspecialchars($t['subject']) ?></td>
    <td><?= htmlspecialchars($t['status']) ?></td>
    <?php if ($_SESSION['role'] !== 'customer'): ?><td><?= htmlspecialchars($t['customer']) ?></td><?php endif; ?>
</tr>
<?php endforeach; ?>
</table>
<?php require __DIR__ . '/../includes/footer.php'; ?>
