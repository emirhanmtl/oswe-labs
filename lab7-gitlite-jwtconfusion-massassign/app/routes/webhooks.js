const express = require('express');
const { PUBLIC_KEY } = require('../lib/jwt');

const router = express.Router();

// gitlite signs a receipt for every webhook delivery it sends out (snippet
// export completed, comment posted, etc.) so integrators can confirm a
// notification actually came from this instance. This endpoint publishes
// the verification key for that - it is meant to be public, the same way a
// JWKS endpoint would be.
router.get('/api/webhooks/public-key', (req, res) => {
  res.type('text/plain').send(PUBLIC_KEY);
});

module.exports = router;
