const React = require('React');
const CopyToClipboard = require('react-copy-to-clipboard');
const {DispatchMixin, SubscribeToStateChangesMixin} = require('../../mixins');
const actions = require('../../../redux/actions');

module.exports = React.createClass({
  mixins: [DispatchMixin, SubscribeToStateChangesMixin],
  onCopy() {
    this.dispatchAction(actions.setInfoMessage('Key copied to clipboard.'));
  },
  render() {
    return (
      <div className="row view-key-page">
        <div className="col-sm-6 col-sm-offset-3">
          <textarea readOnly="true" value={this.state.current_identity.publicKey}>
          </textarea>
        </div>
        <div className="col-sm-6 col-sm-offset-3">
          <CopyToClipboard onCopy={this.onCopy} text={this.state.current_identity.publicKey}>
            <span className="pull-right btn">Copy to Clipboard</span>
          </CopyToClipboard>
        </div>
      </div>
    );
  }
});
