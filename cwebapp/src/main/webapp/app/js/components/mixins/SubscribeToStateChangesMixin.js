const store = require('../../redux/store.js');

module.exports = {
  componentWillMount() {
    this.unsubscribe = store.subscribe(() => {
      this.setState(store.getState());
    });
  },
  getInitialState() {
    return store.getState();
  },
  componentWillUnmount() {
    this.unsubscribe();
  }
};
