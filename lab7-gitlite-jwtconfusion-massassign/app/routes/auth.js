const express = require('express');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const { signToken } = require('../lib/jwt');

const router = express.Router();

// No self-registration on purpose - accounts are provisioned by an admin.
// This is the only way to get a token if you already have a password.
router.post('/api/login', async (req, res) => {
  const { username, password } = req.body || {};
  if (typeof username !== 'string' || typeof password !== 'string' || !username || !password) {
    return res.status(400).json({ error: 'username and password are required' });
  }

  const user = await User.findOne({ username });
  if (!user || !(await bcrypt.compare(password, user.passwordHash))) {
    return res.status(401).json({ error: 'Invalid credentials' });
  }

  const token = signToken({ id: user._id.toString(), username: user.username, role: user.role });
  res.json({ token });
});

module.exports = router;
