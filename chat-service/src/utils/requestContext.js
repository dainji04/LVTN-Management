const { AsyncLocalStorage } = require('async_hooks');

const asyncLocalStorage = new AsyncLocalStorage();

module.exports = {
  asyncLocalStorage,

  run: (data, callback) => {
    asyncLocalStorage.run(data, callback);
  },

  get: () => {
    return asyncLocalStorage.getStore();
  }
};