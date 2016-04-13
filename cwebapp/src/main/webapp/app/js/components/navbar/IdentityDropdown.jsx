const React = require('React');
const activeComponent = require('react-router-active-component');
const { Link } = require('react-router');

module.exports = React.createClass({
  propTypes: {
    currentIdentity: React.PropTypes.any.isRequired,
    userIdentities: React.PropTypes.array.isRequired,
    switchUserIdentity: React.PropTypes.func.isRequired
  },
  onIdentityClick(hash, name) {
    return (() =>
      this.props.switchUserIdentity({hash, name})
    );
  },
  render() {
    const {name: currentName, hash: currentHash} = this.props.currentIdentity;
    return (
      <li className="dropdown">
        <a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
          {currentName} <span className="caret"></span>
        </a>
        <ul className="dropdown-menu">
          {
            this.props.userIdentities
            .filter(({hash}) => (hash !== currentHash))
            .map(({name, hash}) =>
              <li key={hash} data-hash={hash} data-name={name} onClick={this.onIdentityClick(hash, name)}>
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
