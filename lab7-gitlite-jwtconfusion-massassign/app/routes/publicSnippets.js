const express = require('express');
const Snippet = require('../models/Snippet');

const router = express.Router();

// Anyone can browse public snippets without an account, same as a public
// repo/gist listing on a real git host.
router.get('/api/public/snippets', async (req, res) => {
  const snippets = await Snippet.find({ visibility: 'public' })
    .select('title language createdAt authorId')
    .populate('authorId', 'username')
    .sort({ createdAt: -1 });

  res.json(snippets.map((s) => ({
    id: s._id,
    title: s.title,
    language: s.language,
    createdAt: s.createdAt,
    author: { id: s.authorId._id, username: s.authorId.username },
  })));
});

module.exports = router;
