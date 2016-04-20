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
    contentType: 'text/plain'
  });
};

module.exports = actions = {
  addToTorrentList({magnetLink}) {
    return (dispatch, getState) => {
      const {xt, dn} = magnet.decode(magnetLink || '');
      const {publicKey} = getState().current_identity;
      if (!xt) {
        dispatch(actions.setErrorMessage('Invalid magnet link.'));
      } else {
        const hash = xt.split(':').pop();
        dispatch(this.setLoading(true));
        dispatch(this._addToUserTorrentList({hash, publicKey, displayName: dn}));
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
        if (jqXHR.status !== 204) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch(actions._setUpvoted({hash}));
        return persistState(getState());
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error during upvote: ${err}!`));
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
        if (jqXHR.status !== 204) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch(actions._setDownvoted({hash}));
        return persistState(getState());
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error during downvote: ${err}!`));
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
  _addToUserTorrentList({hash, displayName, publicKey}) {
    return {type: 'ADD_TO_TORRENT_LIST', hash, displayName, publicKey};
  },
  addTrustedKey({name, publicKey}) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: '/api/user/trust',
        type: 'POST',
        data: JSON.stringify({publicKey}),
        processData: false,
        contentType: 'application/json'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch({type: 'ADD_TRUSTED_IDENTITY', name, publicKey});
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
  deleteTrustedIdentity(publicKey) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: '/api/user/trust/',
        type: 'DELETE',
        data: JSON.stringify({publicKey}),
        processData: false,
        contentType: 'application/json'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch({type: 'DELETE_TRUSTED_IDENTITY', publicKey});
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
  _serversideSwitchUserIdentity({name, publicKey}) {
    return ((dispatch, getState) =>
      $.ajax({
        url: '/api/identity/switch',
        type: 'POST',
        data: JSON.stringify({publicKey}),
        processData: false,
        contentType: 'application/json',
        dataType: 'json'
      })
    );
  },
  switchUserIdentity({name, publicKey}) {
    return (dispatch, getState) => {
      dispatch(actions._serversideSwitchUserIdentity({name, publicKey}))
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch({type: 'SWITCH_USER_IDENTITY', name, publicKey});
        return persistState(getState());
      })
      .done(() => dispatch(actions.setInfoMessage('User ID updated successfully.')))
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error switching identity: ${err}!`));
      });
    };
  },
  _addUserIdentity({name, publicKey, privateKey}) {
    return {type: 'ADD_USER_IDENTITY', name, publicKey, privateKey};
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
    let publicKey, privateKey;
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      $.ajax({
        url: '/api/identity',
        type: 'POST',
        data: JSON.stringify({name}),
        processData: false,
        contentType: 'application/json',
        dataType: 'json'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        publicKey = data.publicKey;
        privateKey = data.privateKey;
        dispatch(actions._addUserIdentity({name, publicKey, privateKey}));
        return dispatch(actions._serversideSwitchUserIdentity({name, publicKey}));
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        dispatch({type: 'SWITCH_USER_IDENTITY', name, publicKey});
        return persistState(getState());
      })
      .done(() => {
        dispatch(actions.setInfoMessage(`Identity "${name}" created successfully`));
        browserHistory.push('/');
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        dispatch(actions.setErrorMessage(`Error creating identity: ${err}!`));
      })
      .always(() => dispatch(this.setLoading(false)));
    };
  }
};
