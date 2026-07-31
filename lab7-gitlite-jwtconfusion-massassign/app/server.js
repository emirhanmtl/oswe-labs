const express = require('express');
const { connectDb } = require('./lib/db');
const { seed } = require('./lib/seed');

const authRoutes = require('./routes/auth');
const webhookRoutes = require('./routes/webhooks');
const publicSnippetRoutes = require('./routes/publicSnippets');
const snippetRoutes = require('./routes/snippets');
const searchRoutes = require('./routes/search');
const profileRoutes = require('./routes/profile');
const commentRoutes = require('./routes/comments');
const exportRoutes = require('./routes/export');
const adminRoutes = require('./routes/admin');

const app = express();
app.use(express.json());

app.get('/', (req, res) => res.json({ service: 'gitlite', status: 'ok' }));

app.use(authRoutes);
app.use(webhookRoutes);
app.use(publicSnippetRoutes);
app.use(snippetRoutes);
app.use(searchRoutes);
app.use(profileRoutes);
app.use(commentRoutes);
app.use(exportRoutes);
app.use(adminRoutes);

connectDb()
  .then(async () => {
    await seed();
    app.listen(3000, () => console.log('gitlite listening on :3000'));
  })
  .catch((err) => {
    console.error('Mongo connection failed', err);
    process.exit(1);
  });
