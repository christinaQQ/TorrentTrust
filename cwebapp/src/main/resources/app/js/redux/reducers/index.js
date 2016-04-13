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
    case 'SET_TRUST_ALGORITHM': {
      const {name, id} = action;
      return Object.assign({}, state, {current_trust_algorithm: {name, id}});
    }
    case 'SWITCH_USER_IDENTITY': {
      const {name, hash} = action;
      return Object.assign({}, state, {current_identity: {name, hash}});
    }
    case 'ADD_USER_IDENTITY': {
      const {name, hash} = action;
      const user_identities = state.user_identities.slice(0);
      user_identities.push({name, hash});
      return Object.assign({}, state, {user_identities});
    }
  }
  return state;
};

module.exports = mainReducer;
