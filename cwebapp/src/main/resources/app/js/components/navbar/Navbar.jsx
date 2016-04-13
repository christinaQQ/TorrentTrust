const React = require('React');
const { Link } = require('react-router');
const activeComponent = require('react-router-active-component');
const NavItem = activeComponent('li');
const {SubscribeToStateChangesMixin, DispatchMixin} = require('../mixins/index.js');
const IdentityDropdown = require('./IdentityDropdown.jsx');
const actions = require('../../redux/actions/index.js');

module.exports = React.createClass({
  mixins: [SubscribeToStateChangesMixin, DispatchMixin],
  switchUserIdentity({name, pubKey}) {
    this.dispatchAction(actions.switchUserIdentity({name, pubKey}));
  },
  render() {
    return (
      <nav className="navbar navbar-default">
        <div className="container-fluid">

          <div className="navbar-header">
            <button type="button" className="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
              <span className="sr-only">Toggle navigation</span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
            </button>
            <Link className="navbar-brand" to="/">TorrentTrust</Link>
          </div>

          <div className="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul className="nav navbar-nav">
              <NavItem to="/" onlyActiveOnIndex>Home</NavItem>
              <NavItem to="trust-management">Trust Management</NavItem>
            </ul>
            <ul className="nav navbar-nav navbar-right">
              <NavItem to="settings">Settings</NavItem>
              <IdentityDropdown
                currentIdentity={this.state.current_identity}
                userIdentities={this.state.user_identities}
                switchUserIdentity={this.switchUserIdentity}
              />
            </ul>
          </div>
        </div>
      </nav>
    );
  }
});
