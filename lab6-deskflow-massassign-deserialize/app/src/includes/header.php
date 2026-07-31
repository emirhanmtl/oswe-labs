<!DOCTYPE html>
<html>
<head>
    <title>DeskFlow</title>
    <link rel="stylesheet" href="/style/style.css">
</head>
<body>
<header>
    <a href="/dashboard.php" class="brand">DeskFlow</a>
    <?php if (isset($_SESSION['user_id'])): ?>
        <span>Signed in as <?= htmlspecialchars($_SESSION['username']) ?> (<?= htmlspecialchars($_SESSION['role']) ?>)</span>
        <a href="/profile/update.php">My profile</a>
        <?php if ($_SESSION['role'] === 'admin'): ?><a href="/admin/index.php">Admin</a><?php endif; ?>
        <a href="/logout.php">Logout</a>
    <?php endif; ?>
</header>
<main>
