const { browserHistory } = require('react-router');

module.exports = {
  deleteTrustedIdentity(pubKey) {
    return {type: 'DELETE_TRUSTED_IDENTITY', pubKey};
  },
  addTrustedKey({name, pubKey}) {
    return {type: 'ADD_TRUSTED_IDENTITY', name, pubKey};
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
