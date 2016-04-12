const React = require('React');
const SubscribeToStateChangesMixin = require('./mixins/SubscribeToStateChangesMixin.js');
const DispatchMixin = require('./mixins/DispatchMixin.js');
const actions = require('../redux/actions/index.js');

module.exports = React.createClass({
  mixins: [SubscribeToStateChangesMixin, DispatchMixin],
  onSubmit() {

  },
  generateLabels() {
    return this.state.possible_trust_algorithms.map(({id, name}) =>
      <div key={id}>
        <label>
          <input type="radio"
                 defaultChecked={id === this.state.current_trust_algorithm.id}
                 name="algorithm"
                 id={id}
                 value={name} />
          {name}
        </label>
      </div>
    );
  },
  render() {
    return (
      <div>
        <h2>Trust algorithm</h2>
        <form className="settings-form" onSubmit={this.onSubmit}>

        {this.generateLabels()}

        </form>
      </div>
    );
  }
});
