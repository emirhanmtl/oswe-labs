const mongoose = require('mongoose');

async function connectDb() {
  const url = process.env.MONGO_URL || 'mongodb://db:27017/gitlite';
  await mongoose.connect(url);
  return mongoose.connection;
}

module.exports = { connectDb };
