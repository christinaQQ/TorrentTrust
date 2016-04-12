module.exports = {
  deleteTrustedIdentity(hash) {
    return {type: 'DELETE_TRUSTED_IDENTITY', hash};
  },
  addTrustedKey({name, hash}) {
    return {type: 'ADD_TRUSTED_IDENTITY', name, hash};
  }
};
