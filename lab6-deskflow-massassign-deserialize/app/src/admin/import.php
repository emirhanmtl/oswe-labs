<?php
require __DIR__ . '/guard.php';
require __DIR__ . '/../includes/gadgets.php';

$success = null;
$error = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $blob = trim($_POST['blob'] ?? '');
    if ($blob === '') {
        $error = 'Paste a backup blob first.';
    } else {
        $raw = base64_decode($blob, true);
        if ($raw === false) {
            $error = 'Backup blob is not valid base64.';
        } else {
            // Restoring a report just means re-materializing the AuditReport
            // object that export.php handed out - no extra validation needed
            // since it's our own backup format.
            $report = unserialize($raw);
            $success = 'Report restored.';
        }
    }
}
?>
<?php require __DIR__ . '/../includes/header.php'; ?>
<h1>Import Ticket Audit Report</h1>
<?php if ($error): ?><p class="error"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<?php if ($success): ?><p class="success"><?= htmlspecialchars($success) ?></p><?php endif; ?>
<form method="post">
    <label>Backup blob:<br><textarea name="blob" rows="6" cols="80"></textarea></label><br>
    <button type="submit">Restore</button>
</form>
<?php require __DIR__ . '/../includes/footer.php'; ?>
