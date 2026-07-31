const express = require('express');
const Snippet = require('../models/Snippet');
const Comment = require('../models/Comment');
const requireAuth = require('../middleware/requireAuth');

const router = express.Router();

async function canView(snippet, user) {
  if (!snippet) return false;
  if (snippet.visibility === 'public') return true;
  if (snippet.authorId.toString() === user.id) return true;
  return user.role === 'admin';
}

router.get('/api/snippets/:id/comments', requireAuth, async (req, res) => {
  const snippet = await Snippet.findById(req.params.id);
  if (!(await canView(snippet, req.user))) {
    return res.status(404).json({ error: 'Snippet not found' });
  }

  const comments = await Comment.find({ snippetId: snippet._id })
    .populate('authorId', 'username')
    .sort({ createdAt: 1 });

  res.json(comments.map((c) => ({
    id: c._id,
    body: c.body,
    author: c.authorId.username,
    createdAt: c.createdAt,
  })));
});

router.post('/api/snippets/:id/comments', requireAuth, async (req, res) => {
  const snippet = await Snippet.findById(req.params.id);
  if (!(await canView(snippet, req.user))) {
    return res.status(404).json({ error: 'Snippet not found' });
  }

  const body = (req.body && req.body.body ? String(req.body.body) : '').trim();
  if (!body) {
    return res.status(400).json({ error: 'body is required' });
  }

  const comment = await Comment.create({ snippetId: snippet._id, authorId: req.user.id, body });
  res.status(201).json(comment);
});

module.exports = router;
