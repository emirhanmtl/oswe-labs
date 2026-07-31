const express = require('express');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const Snippet = require('../models/Snippet');
const requireAuth = require('../middleware/requireAuth');
const requireAdmin = require('../middleware/requireAdmin');

const router = express.Router();
const SNIPPET_DIR = path.join(__dirname, '..', 'data', 'snippets');
const EXPORT_DIR = path.join(__dirname, '..', 'data', 'exports');

// Bundles a snippet into a distributable export file for the admin's
// "download an archive of this snippet" tool. A real pandoc-based
// Markdown -> PDF pipeline is on the roadmap (see ticket GITLITE-114); for
// now this just shells out to stitch the snippet body into a plain export
// file, which was the fastest way to ship the feature ahead of the RS256
// migration deadline.
router.post('/api/admin/export', requireAuth, requireAdmin, async (req, res) => {
  const { snippetId, filename } = req.body || {};
  if (!snippetId || !filename) {
    return res.status(400).json({ error: 'snippetId and filename are required' });
  }

  const snippet = await Snippet.findById(snippetId);
  if (!snippet) {
    return res.status(404).json({ error: 'Snippet not found' });
  }

  const srcPath = path.join(SNIPPET_DIR, `${snippet._id}.md`);
  fs.writeFileSync(srcPath, `# ${snippet.title}\n\n${snippet.body}\n`);

  const cmd = `cat ${srcPath} > ${EXPORT_DIR}/${filename} && echo "exported ${filename} at $(date)"`;
  try {
    const log = execSync(cmd, { encoding: 'utf8' });
    res.json({ status: 'ok', log });
  } catch (e) {
    res.status(500).json({ error: e.message, log: e.stdout ? e.stdout.toString() : '' });
  }
});

module.exports = router;
