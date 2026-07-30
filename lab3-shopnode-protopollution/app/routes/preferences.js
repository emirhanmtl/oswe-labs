const express = require('express');
const merge = require('../lib/merge');

const router = express.Router();

function requireAuth(req, res, next) {
  if (!req.session.userId) {
    return res.status(401).json({ error: 'Not authenticated' });
  }
  next();
}

// Any logged-in user can update their own display preferences
// (theme, locale, dashboard widgets, etc.) via a partial JSON patch.
router.put('/api/preferences', requireAuth, (req, res) => {
  const current = req.session.preferences || { theme: 'light', locale: 'en' };
  req.session.preferences = merge(current, req.body || {});
  res.json({ preferences: req.session.preferences });
});

module.exports = router;
