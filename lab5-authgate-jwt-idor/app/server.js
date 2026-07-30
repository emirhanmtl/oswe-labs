const express = require('express');
const authRoutes = require('./routes/auth');
const reportsRoutes = require('./routes/reports');
const adminRoutes = require('./routes/admin');

const app = express();
app.use(express.json());

app.use(authRoutes);
app.use(reportsRoutes);
app.use(adminRoutes);

app.get('/', (req, res) => res.json({ service: 'authgate', status: 'ok' }));

app.listen(4000, () => console.log('authgate listening on :4000'));
