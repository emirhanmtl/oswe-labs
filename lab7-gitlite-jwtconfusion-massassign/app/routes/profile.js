const express = require('express');
const User = require('../models/User');
const requireAuth = require('../middleware/requireAuth');
const { signToken } = require('../lib/jwt');

const router = express.Router();

router.get('/api/profile', requireAuth, async (req, res) => {
  const user = await User.findById(req.user.id).select('username role bio createdAt');
  res.json(user);
});

// Self-service profile editor: whatever the client sends gets written
// straight through to the user document, so adding a new self-editable
// field later (bio, avatarUrl, timezone, ...) never needs a backend change.
router.put('/api/profile', requireAuth, async (req, res) => {
  const updates = req.body || {};
  delete updates._id;

  await User.updateOne({ _id: req.user.id }, updates);
  const updated = await User.findById(req.user.id);

  // Tokens carry the role as a claim, so if role changed as a result of
  // this update, reissue one immediately - no separate login step needed
  // to pick up the new privileges.
  const token = signToken({ id: updated._id.toString(), username: updated.username, role: updated.role });

  res.json({
    token,
    profile: { username: updated.username, role: updated.role, bio: updated.bio },
  });
});

module.exports = router;
