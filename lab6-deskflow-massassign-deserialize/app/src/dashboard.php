<?php
session_start();
require __DIR__ . '/includes/auth.php';
require_login();
?>
<?php require __DIR__ . '/includes/header.php'; ?>
<h1>Dashboard</h1>

<?php if ($_SESSION['role'] === 'customer'): ?>
    <p><a href="/tickets/create.php">Open a new ticket</a></p>
    <p><a href="/tickets/list.php">My tickets</a></p>
<?php elseif ($_SESSION['role'] === 'agent'): ?>
    <p><a href="/tickets/list.php">Ticket queue</a></p>
<?php elseif ($_SESSION['role'] === 'admin'): ?>
    <p><a href="/tickets/list.php">All tickets</a></p>
    <p><a href="/admin/index.php">Admin panel</a></p>
<?php endif; ?>

<?php require __DIR__ . '/includes/footer.php'; ?>
