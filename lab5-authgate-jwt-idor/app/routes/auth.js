const express = require('express');
const { signToken } = require('../auth');

const router = express.Router();

// Only regular users can self-register / log in here. There is no route
// that issues an admin token - admin-ness is only ever read from the JWT
// claims by requireAuth('admin') elsewhere.
const USERS = {
  guest: 'guestpass123',
};

router.post('/api/login', (req, res) => {
  const { username, password } = req.body || {};
  if (USERS[username] && USERS[username] === password) {
    const token = signToken({ username, role: 'user' });
    return res.json({ token });
  }
  res.status(401).json({ error: 'Invalid credentials' });
});

module.exports = router;
