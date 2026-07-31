const crypto = require('crypto');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const Snippet = require('../models/Snippet');

// There is no self-registration in gitlite - accounts are provisioned by an
// admin out of band. Seed a couple of accounts so the app isn't empty on
// first boot. Passwords are long random values the attacker is not meant to
// know or crack, same as every other lab in this collection.
async function seed() {
  const count = await User.countDocuments();
  if (count > 0) {
    return;
  }

  const adminPassword = crypto.randomBytes(24).toString('base64');
  const alicePassword = crypto.randomBytes(24).toString('base64');
  const bobPassword = crypto.randomBytes(24).toString('base64');

  const admin = await User.create({
    username: 'admin',
    passwordHash: await bcrypt.hash(adminPassword, 10),
    role: 'admin',
    bio: 'gitlite instance administrator',
  });

  const alice = await User.create({
    username: 'alice',
    passwordHash: await bcrypt.hash(alicePassword, 10),
    role: 'user',
    bio: 'backend dev, mostly Node and Go snippets',
  });

  const bob = await User.create({
    username: 'bob',
    passwordHash: await bcrypt.hash(bobPassword, 10),
    role: 'user',
    bio: 'frontend',
  });

  await Snippet.create({
    title: 'deploy-checklist',
    language: 'markdown',
    body: '# Deploy checklist\n\n- [ ] run tests\n- [ ] tag release\n- [ ] flip the feature flag\n- [ ] watch dashboards for 15 min',
    visibility: 'public',
    authorId: alice._id,
  });

  await Snippet.create({
    title: 'pg-connection-snippet',
    language: 'javascript',
    body: "const { Pool } = require('pg');\nmodule.exports = new Pool({ connectionString: process.env.DATABASE_URL });",
    visibility: 'private',
    authorId: alice._id,
  });

  await Snippet.create({
    title: 'css-reset',
    language: 'css',
    body: '* { margin: 0; padding: 0; box-sizing: border-box; }',
    visibility: 'public',
    authorId: bob._id,
  });

  await Snippet.create({
    title: 'internal-rotation-notes',
    language: 'markdown',
    body: '# On-call rotation\n\nSee the admin panel for the current schedule.',
    visibility: 'private',
    authorId: admin._id,
  });

  console.log('Seeded gitlite with admin/alice/bob (passwords are random and intentionally not logged).');
}

module.exports = { seed };
