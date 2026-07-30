const express = require('express');
const User = require('../models/User');

const router = express.Router();

router.get('/login', (req, res) => {
  res.render('login', { error: null });
});

router.post('/login', async (req, res) => {
  const { username, password } = req.body;

  // Trusts the client to send scalar strings here; query operators like
  // {"$ne": null} pass straight through to the driver.
  const user = await User.findOne({ username, password });

  if (!user) {
    return res.render('login', { error: 'Invalid credentials.' });
  }

  req.session.userId = user._id.toString();
  req.session.username = user.username;
  req.session.role = user.role;
  res.redirect('/dashboard');
});

router.get('/logout', (req, res) => {
  req.session.destroy(() => res.redirect('/login'));
});

module.exports = router;
