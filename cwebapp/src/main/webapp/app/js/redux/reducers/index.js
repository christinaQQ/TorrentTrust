var mainReducer = function (state, action) {
  switch (action.type) {
    case 'DELETE_TRUSTED_IDENTITY': {
      const trusted_identities = state.trusted_identities.filter(({hash}) =>
        hash !== action.hash
      );
      return Object.assign({}, state, {trusted_identities});
    }
    case 'ADD_TRUSTED_IDENTITY': {
      const trusted_identities = state.trusted_identities.slice(0);
      trusted_identities.push({name: action.name, hash: action.hash});
      return Object.assign({}, state, {trusted_identities});
    }
  }
  return state;
};

module.exports = mainReducer;
