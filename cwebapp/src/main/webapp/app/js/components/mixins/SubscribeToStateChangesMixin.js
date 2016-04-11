const store = require('../redux/store.js');

module.exports = {
  componentWillMount() {
    this.unsubscribe = store.subscribe(() => {
      this.setState(store.getState());
    });
  },
  componentWillUnmount() {
    this.unsubscribe();
  }
};
