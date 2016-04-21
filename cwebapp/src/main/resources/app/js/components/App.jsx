const React = require('React');
const Navbar = require('./navbar/Navbar.jsx');
const {DispatchMixin, SubscribeToStateChangesMixin} = require('./mixins');
const actions = require('../redux/actions');
const CreateIdentityPage = require('./pages/CreateIdentityPage');


module.exports = React.createClass({
  mixins: [DispatchMixin, SubscribeToStateChangesMixin],
  getErrorMessageComponent() {
    if (!this.state.error_message) {
      return '';
    }
    return <div className="alert alert-danger" role="alert">
      <button onClick={this.dismissErrorMessage} type="button" className="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      {this.state.error_message}
    </div>;
  },
  getInfoMessageComponent() {
    if (!this.state.info_message) {
      return '';
    }
    return <div className="alert alert-info" role="alert">
      <button onClick={this.dismissInfoMessage} type="button" className="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      {this.state.info_message}
    </div>;
  },
  dismissInfoMessage() {
    this.dispatchAction(actions.setInfoMessage(null));
  },
  dismissErrorMessage() {
    this.dispatchAction(actions.setErrorMessage(null));
  },
  render() {
    return (
      <div>
        {this.state.loading ? <div className="loading">Loading&#8230;</div> : ''}
        <Navbar/>
        <div className="container">
          {this.getErrorMessageComponent()}
          {this.getInfoMessageComponent()}
          {this.state.current_identity ? this.props.children : <CreateIdentityPage/>}
        </div>
      </div>
    );
  }
});
