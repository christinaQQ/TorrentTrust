const React = require('React');
const actions = require('../../../redux/actions/index.js');
const DispatchMixin = require('../../mixins/DispatchMixin.js');
const CreateIdentityForm = require('./form.jsx');

module.exports = React.createClass({
  mixins: [DispatchMixin],
  createNewIdentity(name) {
    this.dispatchAction(actions.createNewIdentity({name}));
    console.log(`dispatching create action with name=${name}`);
  },
  render() {
    return <CreateIdentityForm onFormSubmit={this.createNewIdentity}/>;
  }
});
