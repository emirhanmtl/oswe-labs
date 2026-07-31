const jwt = require('jsonwebtoken');

// Ops was supposed to override this with a real secret via the JWT_SECRET
// env var in production. It was never set on this box.
const JWT_SECRET = process.env.JWT_SECRET || 'authgate_super_secret_2024';

function signToken(payload) {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: '2h' });
}

function requireAuth(role) {
  return (req, res, next) => {
    const header = req.headers.authorization || '';
    const token = header.startsWith('Bearer ') ? header.slice(7) : null;
    if (!token) {
      return res.status(401).json({ error: 'Missing bearer token' });
    }
    try {
      const decoded = jwt.verify(token, JWT_SECRET);
      if (role && decoded.role !== role) {
        return res.status(403).json({ error: 'Insufficient role' });
      }
      req.user = decoded;
      next();
    } catch (e) {
      return res.status(401).json({ error: 'Invalid token' });
    }
  };
}

module.exports = { signToken, requireAuth, JWT_SECRET };
