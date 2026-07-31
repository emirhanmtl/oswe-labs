const express = require('express');
const Snippet = require('../models/Snippet');
const requireAuth = require('../middleware/requireAuth');
const { escapeRegex } = require('../utils/regexEscape');

const router = express.Router();

// Looks like the obvious place to try a NoSQL-injection payload (free-text
// search query dropped into a Mongo filter) - but the term is regex-escaped
// before it's ever used, and the visibility/ownership clause always scopes
// results to snippets this user is allowed to see, so there's nothing here
// to actually inject or bypass with.
router.get('/api/snippets/search', requireAuth, async (req, res) => {
  const q = escapeRegex((req.query.q || '').toString());

  const results = await Snippet.find({
    title: { $regex: q, $options: 'i' },
    $or: [{ visibility: 'public' }, { authorId: req.user.id }],
  }).select('title language visibility authorId createdAt');

  res.json(results);
});

module.exports = router;
