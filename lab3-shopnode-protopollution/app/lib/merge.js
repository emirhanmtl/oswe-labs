// Small recursive merge used to apply partial preference updates on top of
// existing defaults, e.g. merge({theme:'light'}, {theme:'dark'}).
function merge(target, source) {
  for (const key in source) {
    const value = source[key];
    if (value && typeof value === 'object' && !Array.isArray(value)) {
      if (!target[key] || typeof target[key] !== 'object') {
        target[key] = {};
      }
      merge(target[key], value);
    } else {
      target[key] = value;
    }
  }
  return target;
}

module.exports = merge;
