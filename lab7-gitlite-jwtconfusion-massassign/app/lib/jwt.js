const jwt = require('jsonwebtoken');
const crypto = require('crypto');

// RSA keypair generated fresh at boot. The private half signs every JWT this
// app issues for user sessions. The public half is also reused to sign (and
// let integrators verify) the delivery receipts attached to webhook
// notifications - see routes/webhooks.js - which is why it gets published at
// GET /api/webhooks/public-key: third-party integrators need it to check
// that a webhook payload really came from us, without ever calling back in.
const { publicKey: PUBLIC_KEY, privateKey: PRIVATE_KEY } = crypto.generateKeyPairSync('rsa', {
  modulusLength: 2048,
  publicKeyEncoding: { type: 'spki', format: 'pem' },
  privateKeyEncoding: { type: 'pkcs1', format: 'pem' },
});

function signToken(payload, options = {}) {
  return jwt.sign(payload, PRIVATE_KEY, { algorithm: 'RS256', expiresIn: '4h', ...options });
}

// Session tokens are signed RS256. A couple of older internal integrations
// (a legacy cron worker, mostly) still mint HS256 service tokens against a
// shared secret from before the auth module switched to RS256 - rather than
// running two separate verify functions we kept this one permissive about
// which algorithm family it accepts.
function verifyToken(token) {
  return jwt.verify(token, PUBLIC_KEY, { algorithms: ['RS256', 'HS256'] });
}

module.exports = { signToken, verifyToken, PUBLIC_KEY };
