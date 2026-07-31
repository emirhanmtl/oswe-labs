<?php
require __DIR__ . '/guard.php';
?>
<!DOCTYPE html>
<html>
<head><title>NoteVault - Admin</title></head>
<body>
<h1>Admin panel</h1>
<p>Logged in as <?= htmlspecialchars($_SESSION['username']) ?></p>
<p><a href="../notes.php">Back to notes</a></p>

<h2>Update profile avatar</h2>
<form action="upload_avatar.php" method="post" enctype="multipart/form-data">
    <input type="file" name="avatar">
    <button type="submit">Upload</button>
</form>

<h2>Current avatars</h2>
<ul>
<?php foreach (glob(__DIR__ . '/../uploads/*') as $f): if (basename($f) === '.gitkeep') continue; ?>
    <li><a href="../uploads/<?= htmlspecialchars(basename($f)) ?>"><?= htmlspecialchars(basename($f)) ?></a></li>
<?php endforeach; ?>
</ul>
</body>
</html>
