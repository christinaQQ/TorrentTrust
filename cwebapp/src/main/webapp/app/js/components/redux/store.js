const ReduxThunk = require('redux-thunk').default;
const rootReducer = require('./reducers/index.js');

const store = createStore(
  rootReducer,
  applyMiddleware(ReduxThunk)
);

module.exports = store;
