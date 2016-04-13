const { browserHistory } = require('react-router');
const $ = require('jquery');
const magnet = require('magnet-uri');

var actions;
var persistState = function (state) {
  return $.ajax({
    url: 'api/setState',
    type: 'POST',
    data: JSON.stringify(state),
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
        dispatch(this.setErrorMessage('Invalid magnet link.'));
      } else {
        const hash = xt.split(':').pop();
        dispatch(this.setLoading(true));
        dispatch(this._addToUserTorrentList({hash, pubKey, displayName: dn}));
        this.persistState(getState())
          .fail(() => dispatch(this.setErrorMessage('Failed to persist state.')))
          .always(() => dispatch(this.setLoading(false)));
      }
    };
  },
  upvote({hash}) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      const actions = this;
      $.ajax({
        url: `/api/object/${hash}/up`,
        type: 'POST',
        error(_, __, e) {
          dispatch(actions.setErrorMessage(`Error: ${e}`));
        },
        statusCode: {
          400() {
            dispatch(actions.setErrorMessage(`Error: no object exists with hash ${hash}.`));
          }
        }
      })
        .done(() => {
          dispatch(actions._setUpvoted({hash}));
          return persistState(getState())
            .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));
        })
        .always(() => dispatch(actions.setLoading(false)));
    };
  },
  downvote({hash}) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      const actions = this;
      $.ajax({
        url: `/api/object/${hash}/down`,
        type: 'POST',
        error(_, __, e) {
          dispatch(actions.setErrorMessage(`Error: ${e}`));
        },
        statusCode: {
          400() {
            dispatch(actions.setErrorMessage(`Error: no object exists with hash ${hash}.`));
          }
        }
      })
        .done(() => {
          dispatch(actions._setDownvoted({hash}));
          return persistState(getState())
            .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));
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
      const actions = this;
      $.ajax({
        url: '/api/user/trust/',
        type: 'POST',
        data: pubKey,
        processData: false,
        contentType: 'text/plain',
        error(_, __, e) {
          dispatch(actions.setErrorMessage(`Error: ${e}`));
        },
        statusCode: {
          400() {
            dispatch(actions.setErrorMessage(`Error: no user exists with key ${pubKey}.`));
          }
        }
      })
        .done(() => {
          dispatch({type: 'ADD_TRUSTED_IDENTITY', name, pubKey});
          return persistState(getState())
            .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));
        })
        .always(() => dispatch(actions.setLoading(false)));
    };
  },
  deleteTrustedIdentity(pubKey) {
    return (dispatch, getState) => {
      dispatch(this.setLoading(true));
      const actions = this;
      $.ajax({
        url: '/api/user/trust/',
        type: 'DELETE',
        data: pubKey,
        processData: false,
        contentType: 'text/plain',
        error(_, __, e) {
          dispatch(actions.setErrorMessage(`Error: ${e}`));
        },
        statusCode: {
          400() {
            dispatch(actions.setErrorMessage(`Error: no user exists with key ${pubKey}.`));
          }
        }
      })
        .done(() => {
          dispatch({type: 'DELETE_TRUSTED_IDENTITY', pubKey});
          return persistState(getState())
            .fail(() => dispatch(actions.setErrorMessage('Failed to persist state.')));
        })
        .always(() => dispatch(actions.setLoading(false)));
    };
  },
  setTrustAlgorithm({name, id}) {
    return {type: 'SET_TRUST_ALGORITHM', name, id};
  },
  switchUserIdentity({name, pubKey}) {
    return {type: 'SWITCH_USER_IDENTITY', name, pubKey};
  },
  _addUserIdentity({name, pubKey}) {
    return {type: 'ADD_USER_IDENTITY', name, pubKey};
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
    return dispatch => {
      const pubKey = Array(36).join().split(',').map(() => '123456789abcdef'.charAt(Math.floor(Math.random() * 16))).join('');
      dispatch(this._addUserIdentity({name, pubKey}));
      dispatch(this.switchUserIdentity({name, pubKey}));
      dispatch(this.setInfoMessage(`Identity "${name}" created successfully`));
      // TODO display a success method
      browserHistory.push('/'); // navigate back to root
    };
  }
};
