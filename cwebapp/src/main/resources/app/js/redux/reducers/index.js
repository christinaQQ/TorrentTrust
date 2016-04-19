var mainReducer = function (state, action) {
  switch (action.type) {
    case 'DELETE_TRUSTED_IDENTITY': {
      const {pubKey} = state.current_identity;
      const trusted_identities = JSON.parse(JSON.stringify(state.trusted_identities));
      trusted_identities[pubKey] = state.trusted_identities[pubKey].filter(id =>
        id.pubKey !== action.pubKey
      );
      return Object.assign({}, state, {trusted_identities});
    }
    case 'ADD_TRUSTED_IDENTITY': {
      const {pubKey} = state.current_identity;
      const trusted_identities = JSON.parse(JSON.stringify(state.trusted_identities));
      trusted_identities[pubKey].push({name: action.name, pubKey: action.pubKey});
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
      const trusted_identities = Object.assign({}, state.trusted_identities);
      user_identities.push({name, pubKey});
      torrent_lists[pubKey] = [];
      trusted_identities[pubKey] = [];
      return Object.assign({}, state, {user_identities, torrent_lists, trusted_identities});
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
    case 'ADD_TO_TORRENT_LIST': {
      const {hash, displayName, pubKey} = action;
      const torrent_lists = Object.assign({}, state.torrent_lists);
      torrent_lists[pubKey].push({hash, displayName, upvoted: false, downvoted: false});
      return Object.assign({}, state, {torrent_lists});
    }
    case 'SET_UPVOTED': {
      const {hash} = action;
      const {pubKey} = state.current_identity;
      const torrent_lists = JSON.parse(JSON.stringify(state.torrent_lists));
      torrent_lists[pubKey].filter(torrent => torrent.hash === hash)[0].upvoted = true;
      torrent_lists[pubKey].filter(torrent => torrent.hash === hash)[0].downvoted = false;
      return Object.assign({}, state, {torrent_lists});
    }
    case 'SET_DOWNVOTED': {
      const {hash} = action;
      const {pubKey} = state.current_identity;
      const torrent_lists = JSON.parse(JSON.stringify(state.torrent_lists));
      torrent_lists[pubKey].filter(torrent => torrent.hash === hash)[0].upvoted = false;
      torrent_lists[pubKey].filter(torrent => torrent.hash === hash)[0].downvoted = true;
      return Object.assign({}, state, {torrent_lists});
    }
  }
  return state;
};

module.exports = mainReducer;
