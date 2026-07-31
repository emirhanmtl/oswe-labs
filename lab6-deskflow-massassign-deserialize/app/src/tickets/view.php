<?php
session_start();
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/auth.php';
require_login();

$pdo = get_db();
$id = (int)($_GET['id'] ?? 0);

$stmt = $pdo->prepare('SELECT t.*, u.username AS customer_username FROM tickets t JOIN users u ON u.id = t.customer_id WHERE t.id = ?');
$stmt->execute([$id]);
$ticket = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$ticket) {
    http_response_code(404);
    die('Ticket not found.');
}

if ($_SESSION['role'] === 'customer' && (int)$ticket['customer_id'] !== (int)current_user_id()) {
    http_response_code(403);
    die('Forbidden.');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['body'])) {
    $body = trim($_POST['body']);
    if ($body !== '') {
        $stmt = $pdo->prepare('INSERT INTO ticket_comments (ticket_id, author_id, body) VALUES (?, ?, ?)');
        $stmt->execute([$id, current_user_id(), $body]);
    }
    header('Location: /tickets/view.php?id=' . $id);
    exit;
}

$stmt = $pdo->prepare('SELECT c.body, c.created_at, u.username FROM ticket_comments c JOIN users u ON u.id = c.author_id WHERE c.ticket_id = ? ORDER BY c.created_at ASC');
$stmt->execute([$id]);
$comments = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1><?= htmlspecialchars($ticket['subject']) ?></h1>
<p>Status: <?= htmlspecialchars($ticket['status']) ?> | Customer: <?= htmlspecialchars($ticket['customer_username']) ?></p>

<div id="comments">
<?php foreach ($comments as $c): ?>
    <div class="comment">
        <strong><?= htmlspecialchars($c['username']) ?></strong>
        <p><?= htmlspecialchars($c['body']) ?></p>
    </div>
<?php endforeach; ?>
</div>

<form method="post">
    <label>Add a comment (type @ to tag a teammate):<br>
        <textarea id="comment-body" name="body" rows="4" cols="60"></textarea>
    </label><br>
    <div id="mention-results"></div>
    <button type="submit">Post comment</button>
</form>

<script>
// Lightweight @mention lookup so agents can tag a teammate on a ticket.
document.getElementById('comment-body').addEventListener('input', function (e) {
    var match = /@(\w*)$/.exec(e.target.value);
    var box = document.getElementById('mention-results');
    if (!match) { box.innerHTML = ''; return; }
    fetch('/tickets/mention_lookup.php?q=' + encodeURIComponent(match[1]))
        .then(function (r) { return r.json(); })
        .then(function (users) {
            box.innerHTML = users.map(function (u) { return '<span class="mention">@' + u.username + '</span>'; }).join(' ');
        });
});
</script>

<?php require __DIR__ . '/../includes/footer.php'; ?>
