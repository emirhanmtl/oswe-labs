// Escapes regex metacharacters so a user-supplied search term can be dropped
// into a MongoDB $regex filter as a literal substring match instead of an
// arbitrary pattern.
function escapeRegex(str) {
  return String(str).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

module.exports = { escapeRegex };
