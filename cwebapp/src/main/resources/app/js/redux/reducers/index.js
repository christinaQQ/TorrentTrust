var mainReducer = function (state, action) {
  switch (action.type) {
    case 'DELETE_TRUSTED_IDENTITY': {
      const trusted_identities = state.trusted_identities.filter(({pubKey}) =>
        pubKey !== action.pubKey
      );
      return Object.assign({}, state, {trusted_identities});
    }
    case 'ADD_TRUSTED_IDENTITY': {
      const trusted_identities = state.trusted_identities.slice(0);
      trusted_identities.push({name: action.name, pubKey: action.pubKey});
      return Object.assign({}, state, {trusted_identities});
    }
    case 'SET_TRUST_ALGORITHM': {
      const {name, id} = action;
      return Object.assign({}, state, {current_trust_algorithm: {name, id}});
    }
    case 'SWITCH_USER_IDENTITY': {
      const {pubKey} = action;
      const newIdentity = state.user_identities.filter(id => id.pubKey === pubKey)[0];
      return Object.assign({}, state, {current_identity: newIdentity});
    }
    case 'ADD_USER_IDENTITY': {
      const {name, pubKey} = action;
      const user_identities = state.user_identities.slice(0);
      const torrent_lists = Object.assign({}, state.torrent_lists);
      user_identities.push({name, pubKey});
      torrent_lists[pubKey] = [];
      return Object.assign({}, state, {user_identities, torrent_lists});
    }
    case 'SET_INFO_MESSAGE': {
      const {newMessage} = action;
      return Object.assign({}, state, {info_message: newMessage});
    }
    case 'SET_ERROR_MESSAGE': {
      const {newMessage} = action;
      return Object.assign({}, state, {error_message: newMessage});
    }
    case 'SET_LOADING': {
      return Object.assign({}, state, {loading: action.value});
    }
  }
  return state;
};

module.exports = mainReducer;
