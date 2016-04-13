const React = require('React');
const actions = require('../../redux/actions/index.js');
const DispatchMixin = require('../mixins/DispatchMixin.js');

module.exports = React.createClass({
  mixins: [DispatchMixin],
  propTypes: {
    name: React.PropTypes.string.isRequired,
    hash: React.PropTypes.string.isRequired
  },
  onDeleteButtonClick() {
    this.dispatchAction(actions.deleteTrustedIdentity(this.props.hash));
  },
  render() {
    return (
      <p>
        {this.props.name} ({this.props.hash.substring(0, 8)}...) &nbsp;
        <button onClick={this.onDeleteButtonClick}
                className="btn delete-button rounded-button">
                Delete
        </button>
      </p>
    );
  }
});
