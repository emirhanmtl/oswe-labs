const express = require('express');
const User = require('../models/User');
const Snippet = require('../models/Snippet');
const requireAuth = require('../middleware/requireAuth');
const requireAdmin = require('../middleware/requireAdmin');

const router = express.Router();

router.get('/api/admin/users', requireAuth, requireAdmin, async (req, res) => {
  const users = await User.find({}).select('username role bio createdAt');
  res.json(users);
});

router.get('/api/admin/snippets', requireAuth, requireAdmin, async (req, res) => {
  const snippets = await Snippet.find({}).populate('authorId', 'username').sort({ createdAt: -1 });
  res.json(snippets.map((s) => ({
    id: s._id,
    title: s.title,
    visibility: s.visibility,
    author: s.authorId ? s.authorId.username : null,
    createdAt: s.createdAt,
  })));
});

module.exports = router;
