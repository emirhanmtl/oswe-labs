const path = require('path');
const crypto = require('crypto');
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');

const User = require('./models/User');
const authRoutes = require('./routes/auth');
const dashboardRoutes = require('./routes/dashboard');
const preferencesRoutes = require('./routes/preferences');

const app = express();

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));
app.use(session({
  secret: 'shopnode-session-secret',
  resave: false,
  saveUninitialized: false,
}));

app.use(authRoutes);
app.use(dashboardRoutes);
app.use(preferencesRoutes);

app.get('/', (req, res) => res.redirect('/login'));

const MONGO_URL = process.env.MONGO_URL || 'mongodb://db:27017/shopnode';

async function seed() {
  const count = await User.countDocuments();
  if (count === 0) {
    const adminPassword = crypto.randomBytes(18).toString('base64');
    const userPassword = crypto.randomBytes(18).toString('base64');
    await User.create({ username: 'admin', password: adminPassword, role: 'admin' });
    await User.create({ username: 'jsmith', password: userPassword, role: 'user' });
    console.log('Seeded users (passwords are random and intentionally not logged for real).');
  }
}

mongoose.connect(MONGO_URL).then(async () => {
  await seed();
  app.listen(3000, () => console.log('shopnode listening on :3000'));
}).catch((err) => {
  console.error('Mongo connection failed', err);
  process.exit(1);
});
