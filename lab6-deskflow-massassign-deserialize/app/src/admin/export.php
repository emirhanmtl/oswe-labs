<?php
require __DIR__ . '/guard.php';
require __DIR__ . '/../db.php';
require __DIR__ . '/../includes/gadgets.php';

$pdo = get_db();
$tickets = $pdo->query('SELECT id, subject FROM tickets ORDER BY id')->fetchAll(PDO::FETCH_ASSOC);
$downloadBlob = null;
$ticketId = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $ticketId = (int)($_POST['ticket_id'] ?? 0);

    $stmt = $pdo->prepare('SELECT subject FROM tickets WHERE id = ?');
    $stmt->execute([$ticketId]);
    $subject = $stmt->fetchColumn();

    $stmt = $pdo->prepare('SELECT u.username, c.body, c.created_at FROM ticket_comments c JOIN users u ON u.id = c.author_id WHERE c.ticket_id = ? ORDER BY c.created_at ASC');
    $stmt->execute([$ticketId]);
    $lines = ["Audit report for ticket #$ticketId: $subject", ''];
    foreach ($stmt->fetchAll(PDO::FETCH_ASSOC) as $row) {
        $lines[] = "[{$row['created_at']}] {$row['username']}: {$row['body']}";
    }

    // Written to disk immediately (for the local reports/ archive)...
    $report = new AuditReport(__DIR__ . "/../reports/ticket_{$ticketId}_audit.txt", implode("\n", $lines));

    // ...and also handed back as a portable backup blob, so this same report
    // can be restored on another DeskFlow instance via the import tool.
    $downloadBlob = base64_encode(serialize($report));
}
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>Export Ticket Audit Report</h1>
<form method="post">
    <label>Ticket:
        <select name="ticket_id">
            <?php foreach ($tickets as $t): ?>
                <option value="<?= (int)$t['id'] ?>"><#<?= (int)$t['id'] ?> <?= htmlspecialchars($t['subject']) ?>></option>
            <?php endforeach; ?>
        </select>
    </label>
    <button type="submit">Generate report</button>
</form>

<?php if ($downloadBlob): ?>
<h2>Backup blob for ticket #<?= (int)$ticketId ?></h2>
<p>Save this and use <a href="/admin/import.php">Import</a> to restore it later.</p>
<textarea rows="6" cols="80" readonly><?= htmlspecialchars($downloadBlob) ?></textarea>
<?php endif; ?>

<?php require __DIR__ . '/../includes/footer.php'; ?>
