const React = require('React');
const CopyToClipboard = require('react-copy-to-clipboard');
const {SubscribeToStateChangesMixin} = require('../../mixins');

module.exports = React.createClass({
  mixins: [SubscribeToStateChangesMixin],
  // TODO add success message on copy
  render() {
    return (
      <div className="row view-key-page">
        <div className="col-sm-6 col-sm-offset-3">
          <textarea readOnly="true" value={this.state.current_identity.hash}>
          </textarea>
        </div>
        <div className="col-sm-6 col-sm-offset-3">
          <CopyToClipboard text={this.state.current_identity.hash}>
            <a href="#" className="pull-right btn">Copy to Clipboard</a>
          </CopyToClipboard>
        </div>
      </div>
    );
  }
});
