const express = require('express');
const fs = require('fs');
const path = require('path');
const { requireAuth } = require('../auth');

const router = express.Router();
const PLUGINS_DIR = path.join(__dirname, '..', 'reports', 'plugins');

// Reviewed, approved reporting plugins only - regular users can't write here
// directly, they can only save to their own user_reports folder.
router.get('/api/admin/plugins', requireAuth('admin'), (req, res) => {
  const files = fs.readdirSync(PLUGINS_DIR);
  res.json({ files });
});

router.get('/api/admin/run-plugin', requireAuth('admin'), (req, res) => {
  const { name } = req.query;
  if (!name) {
    return res.status(400).json({ error: 'name query param required' });
  }
  try {
    const modulePath = path.join(PLUGINS_DIR, name);
    delete require.cache[require.resolve(modulePath)];
    const plugin = require(modulePath);
    const output = plugin.run();
    res.json({ output });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
