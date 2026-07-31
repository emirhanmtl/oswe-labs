const mongoose = require('mongoose');

const snippetSchema = new mongoose.Schema({
  title: { type: String, required: true },
  body: { type: String, required: true },
  language: { type: String, default: 'text' },
  visibility: { type: String, default: 'private' }, // private | public
  authorId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  createdAt: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Snippet', snippetSchema);
