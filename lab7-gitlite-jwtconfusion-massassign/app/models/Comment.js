const mongoose = require('mongoose');

const commentSchema = new mongoose.Schema({
  snippetId: { type: mongoose.Schema.Types.ObjectId, ref: 'Snippet', required: true },
  authorId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  body: { type: String, required: true },
  createdAt: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Comment', commentSchema);
