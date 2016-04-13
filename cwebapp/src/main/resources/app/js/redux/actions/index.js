const { browserHistory } = require('react-router');

module.exports = {
  deleteTrustedIdentity(hash) {
    return {type: 'DELETE_TRUSTED_IDENTITY', hash};
  },
  addTrustedKey({name, hash}) {
    return {type: 'ADD_TRUSTED_IDENTITY', name, hash};
  },
  setTrustAlgorithm({name, id}) {
    return {type: 'SET_TRUST_ALGORITHM', name, id};
  },
  switchUserIdentity({name, hash}) {
    return {type: 'SWITCH_USER_IDENTITY', name, hash};
  },
  _addUserIdentity({name, hash}) {
    return {type: 'ADD_USER_IDENTITY', name, hash};
  },
  createNewIdentity({name}) {
    return dispatch => {
      const hash = Array(36).join().split(',').map(() => '123456789abcdef'.charAt(Math.floor(Math.random() * 16))).join('');
      console.log(hash);
      dispatch(this._addUserIdentity({name, hash}));
      dispatch(this.switchUserIdentity({name, hash}));
      // TODO display a success method
      browserHistory.push('/'); // navigate back to root
    };
  }
};
