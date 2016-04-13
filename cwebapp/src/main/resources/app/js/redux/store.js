const ReduxThunk = require('redux-thunk').default;
const rootReducer = require('./reducers/index.js');
const {createStore, applyMiddleware} = require('redux');
const initialState = require('./initialState.js');

const store = createStore(
  rootReducer,
  initialState,
  applyMiddleware(ReduxThunk)
);

module.exports = store;
