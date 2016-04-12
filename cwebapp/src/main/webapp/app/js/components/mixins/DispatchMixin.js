const store = require('../../redux/store.js');

module.exports = {
  dispatchAction(action) {
    store.dispatch(action);
  }
};
