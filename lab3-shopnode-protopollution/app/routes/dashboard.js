const express = require('express');
const router = express.Router();

function requireAuth(req, res, next) {
  if (!req.session.userId) {
    return res.redirect('/login');
  }
  next();
}

router.get('/dashboard', requireAuth, (req, res) => {
  res.render('dashboard', {
    username: req.session.username,
    role: req.session.role,
    preferences: req.session.preferences || { theme: 'light' },
  });
});

module.exports = router;
