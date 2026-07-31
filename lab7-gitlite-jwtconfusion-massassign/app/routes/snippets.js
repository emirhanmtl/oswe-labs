const express = require('express');
const Snippet = require('../models/Snippet');
const requireAuth = require('../middleware/requireAuth');
const { slugify } = require('../utils/slugify');

const router = express.Router();

router.get('/api/snippets', requireAuth, async (req, res) => {
  const snippets = await Snippet.find({ authorId: req.user.id }).sort({ createdAt: -1 });
  res.json(snippets);
});

router.post('/api/snippets', requireAuth, async (req, res) => {
  const { title, body, language, visibility } = req.body || {};
  if (!title || !body) {
    return res.status(400).json({ error: 'title and body are required' });
  }

  const snippet = await Snippet.create({
    title,
    body,
    language: language || 'text',
    visibility: visibility === 'public' ? 'public' : 'private',
    authorId: req.user.id,
  });

  res.status(201).json({ ...snippet.toObject(), slug: slugify(title) });
});

router.get('/api/snippets/:id', requireAuth, async (req, res) => {
  const snippet = await Snippet.findById(req.params.id);
  if (!snippet) {
    return res.status(404).json({ error: 'Snippet not found' });
  }

  const isOwner = snippet.authorId.toString() === req.user.id;
  const isAdmin = req.user.role === 'admin';
  if (snippet.visibility !== 'public' && !isOwner && !isAdmin) {
    return res.status(403).json({ error: 'Forbidden' });
  }

  res.json(snippet);
});

module.exports = router;
