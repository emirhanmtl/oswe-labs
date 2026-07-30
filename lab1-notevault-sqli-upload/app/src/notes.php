<?php
session_start();
require __DIR__ . '/db.php';

if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

$pdo = get_db();
$search = $_GET['search'] ?? '';

if ($search !== '') {
    // Free-text title search across the user's own notes.
    $sql = "SELECT id, title, body FROM notes WHERE user_id = {$_SESSION['user_id']} AND title LIKE '%$search%'";
    $stmt = $pdo->query($sql);
    $notes = $stmt ? $stmt->fetchAll(PDO::FETCH_ASSOC) : [];
} else {
    $stmt = $pdo->prepare("SELECT id, title, body FROM notes WHERE user_id = ?");
    $stmt->execute([$_SESSION['user_id']]);
    $notes = $stmt->fetchAll(PDO::FETCH_ASSOC);
}
?>
<!DOCTYPE html>
<html>
<head><title>NoteVault - Notes</title></head>
<body>
<h1>Welcome, <?= htmlspecialchars($_SESSION['username']) ?> (<?= htmlspecialchars($_SESSION['role']) ?>)</h1>
<p><a href="logout.php">Logout</a><?php if ($_SESSION['role'] === 'admin'): ?> | <a href="admin/index.php">Admin panel</a><?php endif; ?></p>

<form method="get">
    <input type="text" name="search" placeholder="Search notes by title" value="<?= htmlspecialchars($search) ?>">
    <button type="submit">Search</button>
</form>

<h2>Notes</h2>
<table border="1" cellpadding="6">
<tr><th>ID</th><th>Title</th><th>Body</th></tr>
<?php foreach ($notes as $note): ?>
<tr>
    <td><?= htmlspecialchars($note['id']) ?></td>
    <td><?= htmlspecialchars($note['title']) ?></td>
    <td><?= htmlspecialchars($note['body']) ?></td>
</tr>
<?php endforeach; ?>
</table>
</body>
</html>
