const React = require('React');
const actions = require('../../redux/actions/index.js');
const DispatchMixin = require('../mixins/DispatchMixin.js');

module.exports = React.createClass({
  mixins: [DispatchMixin],
  propTypes: {
    name: React.PropTypes.string.isRequired,
    pubKey: React.PropTypes.string.isRequired
  },
  onDeleteButtonClick() {
    this.dispatchAction(actions.deleteTrustedIdentity(this.props.pubKey));
  },
  render() {
    return (
      <p>
        {this.props.name} ({this.props.pubKey.substring(0, 8)}...) &nbsp;
        <button onClick={this.onDeleteButtonClick}
                className="btn delete-button rounded-button">
                Delete
        </button>
      </p>
    );
  }
});
