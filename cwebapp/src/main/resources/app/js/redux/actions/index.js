const { browserHistory } = require('react-router');
const $ = require('jquery');
const magnet = require('magnet-uri');

var actions;
var persistState = function (state) {
  const newState = Object.assign({}, state, {
    error_message: null,
    info_message: null,
    loading: false
  });
  return $.ajax({
    url: 'api/setState',
    type: 'POST',
    data: JSON.stringify(newState, null, '\t'),
    processData: false,
    contentType: 'application/json'
  });
};

module.exports = actions = {
  addToTorrentList({magnetLink}) {
    return (dispatch, getState) => {
      const {xt, dn} = magnet.decode(magnetLink || '');
      const {pubKey} = getState().current_identity;
      if (!xt) {
        dispatch(actions.setErrorMessage('Invalid magnet link.'));
      } else {
        const hash = xt.split(':').pop();
        dispatch(this.setLoading(true));
        dispatch(this._addToUserTorrentList({hash, pubKey, displayName: dn}));
        persistState(getState())
        .fail(() => dispatch(this.setErrorMessage('Failed to persist state.')))
        .always(() => dispatch(this.setLoading(false)));
      }
    };
  },
  upvote({hash}) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: `/api/object/${hash}/up`,
        type: 'POST'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch(actions._setUpvoted({hash}));
        return persistState(getState());
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error creating identity: ${err}!`));
      })
      .always(() => dispatch(actions.setLoading(false)));
    };
  },
  downvote({hash}) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: `/api/object/${hash}/down`,
        type: 'POST'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch(actions._setDownvoted({hash}));
        return persistState(getState());
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error creating identity: ${err}!`));
      })
      .always(() => dispatch(actions.setLoading(false)));
    };
  },
  _setUpvoted({hash}) {
    return {type: 'SET_UPVOTED', hash};
  },
  _setDownvoted({hash}) {
    return {type: 'SET_DOWNVOTED', hash};
  },
  _addToUserTorrentList({hash, displayName, pubKey}) {
    return {type: 'ADD_TO_TORRENT_LIST', hash, displayName, pubKey};
  },
  addTrustedKey({name, pubKey}) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: '/api/user/trust',
        type: 'POST',
        data: pubKey,
        processData: false,
        contentType: 'text/plain'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch({type: 'ADD_TRUSTED_IDENTITY', name, pubKey});
        return persistState(getState());
      })
      .done(() =>
        dispatch(actions.setInfoMessage(`${name} added to trusted keys.`))
      )
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error creating identity: ${err}!`));
      })
      .always(() => dispatch(actions.setLoading(false)));
    };
  },
  deleteTrustedIdentity(pubKey) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: '/api/user/trust/',
        type: 'DELETE',
        data: pubKey,
        processData: false,
        contentType: 'text/plain'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch({type: 'DELETE_TRUSTED_IDENTITY', pubKey});
        return persistState(getState());
      })
      .done(
        () => dispatch(actions.setInfoMessage('Deleted key.'))
      )
      .fail(
        () => dispatch(actions.setErrorMessage('Failed to persist state.'))
      )
      .always(() => dispatch(actions.setLoading(false)));
    };
  },
  setTrustAlgorithm({name, id}) {
    return (dispatch, getState) => {
      dispatch({type: 'SET_TRUST_ALGORITHM', name, id});
      persistState(getState())
      .done(() => dispatch(actions.setInfoMessage('Trust algorithm updated successfully.')))
      .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));

    };
  },
  switchUserIdentity({name, pubKey}) {
    return (dispatch, getState) => {
      dispatch({type: 'SWITCH_USER_IDENTITY', name, pubKey});
      persistState(getState())
      .done(() => dispatch(actions.setInfoMessage('User ID updated successfully.')))
      .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));
    };
  },
  _addUserIdentity({name, pubKey, privateKey}) {
    return {type: 'ADD_USER_IDENTITY', name, pubKey, privateKey};
      // return persistState(getState())
      //   .done(() => dispatch(actions.setInfoMessage(`Identity ${name} added.`)))
      //   .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));
  },
  setInfoMessage(msg) {
    return {type: 'SET_INFO_MESSAGE', newMessage: msg};
  },
  setErrorMessage(msg) {
    return {type: 'SET_ERROR_MESSAGE', newMessage: msg};
  },
  setLoading(value) {
    return {type: 'SET_LOADING', value};
  },
  createNewIdentity({name}) {
    return (dispatch, getState) => {
      $.ajax({
        url: '/api/identity',
        type: 'POST'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        const {pubKey, privateKey} = data;
        dispatch(actions._addUserIdentity({name, pubKey, privateKey}));
        dispatch(actions.switchUserIdentity({name, pubKey}));
        dispatch({type: 'SWITCH_USER_IDENTITY', name, pubKey});
        return persistState(getState());
      })
      .done(() => {
        dispatch(actions.setInfoMessage(`Identity "${name}" created successfully`));
        browserHistory.push('/');
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error creating identity: ${err}!`));
      });
    };
  }
};
