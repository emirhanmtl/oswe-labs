<?php
session_start();
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/auth.php';
require_role('customer');

$error = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $subject = trim($_POST['subject'] ?? '');
    $body = trim($_POST['body'] ?? '');
    if ($subject === '' || $body === '') {
        $error = 'Subject and description are required.';
    } else {
        $pdo = get_db();
        $pdo->beginTransaction();
        $stmt = $pdo->prepare('INSERT INTO tickets (customer_id, subject) VALUES (?, ?) RETURNING id');
        $stmt->execute([current_user_id(), $subject]);
        $ticketId = $stmt->fetchColumn();

        $stmt = $pdo->prepare('INSERT INTO ticket_comments (ticket_id, author_id, body) VALUES (?, ?, ?)');
        $stmt->execute([$ticketId, current_user_id(), $body]);
        $pdo->commit();

        header('Location: /tickets/view.php?id=' . $ticketId);
        exit;
    }
}
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>New Ticket</h1>
<?php if ($error): ?><p class="error"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post">
    <label>Subject: <input type="text" name="subject"></label><br>
    <label>Description:<br><textarea name="body" rows="6" cols="60"></textarea></label><br>
    <button type="submit">Submit</button>
</form>
<?php require __DIR__ . '/../includes/footer.php'; ?>
