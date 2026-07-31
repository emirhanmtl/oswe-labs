<?php
session_start();
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/auth.php';
require_role(['agent', 'admin']);

$pdo = get_db();
$q = trim($_GET['q'] ?? '');
$results = [];

if ($q !== '') {
    $stmt = $pdo->prepare('SELECT t.id, t.subject, t.status, u.username AS customer FROM tickets t JOIN users u ON u.id = t.customer_id WHERE t.subject ILIKE ? ORDER BY t.created_at DESC');
    $stmt->execute(['%' . $q . '%']);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
}
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>Search Tickets</h1>
<form method="get">
    <input type="text" name="q" value="<?= htmlspecialchars($q) ?>" placeholder="Search by subject">
    <button type="submit">Search</button>
</form>
<table border="1" cellpadding="6">
<tr><th>ID</th><th>Subject</th><th>Status</th><th>Customer</th></tr>
<?php foreach ($results as $t): ?>
<tr>
    <td><a href="/tickets/view.php?id=<?= (int)$t['id'] ?>"><?= (int)$t['id'] ?></a></td>
    <td><?= htmlspecialchars($t['subject']) ?></td>
    <td><?= htmlspecialchars($t['status']) ?></td>
    <td><?= htmlspecialchars($t['customer']) ?></td>
</tr>
<?php endforeach; ?>
</table>
<?php require __DIR__ . '/../includes/footer.php'; ?>
