const express = require('express');
const fs = require('fs');
const path = require('path');
const { requireAuth } = require('../auth');

const router = express.Router();
const USER_REPORTS_DIR = path.join(__dirname, '..', 'reports', 'user_reports');

router.get('/api/reports', requireAuth('user'), (req, res) => {
  const files = fs.readdirSync(USER_REPORTS_DIR);
  res.json({ files });
});

// Lets a user save a small text report under their own reports folder.
router.post('/api/reports', requireAuth('user'), (req, res) => {
  const { filename, content } = req.body || {};
  if (!filename || typeof content !== 'string') {
    return res.status(400).json({ error: 'filename and content are required' });
  }

  const destination = path.join(USER_REPORTS_DIR, filename);
  fs.writeFileSync(destination, content);
  res.json({ saved: destination });
});

module.exports = router;
