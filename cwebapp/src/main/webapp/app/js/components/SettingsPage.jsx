const React = require('React');
const {SubscribeToStateChangesMixin, DispatchMixin} = require('./mixins');
const SettingsPageForm = require('./SettingsPageForm.jsx');
const actions = require('../redux/actions');

module.exports = React.createClass({
  mixins: [SubscribeToStateChangesMixin, DispatchMixin],
  dispatchFormSubmit(id, name) {
    this.dispatchAction(actions.setTrustAlgorithm({id, name}));
  },
  generateLabels() {
    return this.state.possible_trust_algorithms.map(({id, name}) =>
      <div key={id}>
        <label>
          <input type="radio"
                 defaultChecked={id === this.state.current_trust_algorithm.id}
                 name="algorithm"
                 id={id}
                 value={name}
                 onClick={this.onRadioClick} />
          {name}
        </label>
      </div>
    );
  },
  render() {
    return (
      <div>
        <h2>Trust algorithm</h2>
        <SettingsPageForm
          onFormSubmit={this.dispatchFormSubmit}
          currentTrustAlgorithm={this.state.current_trust_algorithm}
          possibleTrustAlgorithms={this.state.possible_trust_algorithms}
        />
      </div>
    );
  }
});
