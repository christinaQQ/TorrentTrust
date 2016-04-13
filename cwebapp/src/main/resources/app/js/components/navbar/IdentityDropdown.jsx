const React = require('React');
const activeComponent = require('react-router-active-component');
const { Link } = require('react-router');

module.exports = React.createClass({
  propTypes: {
    currentIdentity: React.PropTypes.any.isRequired,
    userIdentities: React.PropTypes.array.isRequired,
    switchUserIdentity: React.PropTypes.func.isRequired
  },
  onIdentityClick(pubKey, name) {
    return (() =>
      this.props.switchUserIdentity({pubKey, name})
    );
  },
  render() {
    const {name: currentName, pubKey: currentPubKey} = this.props.currentIdentity;
    return (
      <li className="dropdown">
        <a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
          {currentName} <span className="caret"></span>
        </a>
        <ul className="dropdown-menu">
          {
            this.props.userIdentities
            .filter(({pubKey}) => (pubKey !== currentPubKey))
            .map(({name, pubKey}) =>
              <li key={pubKey} data-pubkey={pubKey} data-name={name} onClick={this.onIdentityClick(pubKey, name)}>
                <a href="#">{name}</a>
              </li>
          )}
          <li role="separator" className="divider"></li>
          <li><Link to="/newIdentity">Create new identity</Link></li>
          <li role="separator" className="divider"></li>
          <li><Link to="/currentKey">Your key</Link></li>
        </ul>
      </li>
    );
  }
});
