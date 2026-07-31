// Turns a snippet title into a filesystem/URL-safe slug. Used purely for
// display/cosmetic purposes (e.g. a future "share by slug" link) - nothing
// downstream of this treats the slug as a trusted path component on its own.
function slugify(title) {
  return String(title)
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/(^-+|-+$)/g, '') || 'snippet';
}

module.exports = { slugify };
