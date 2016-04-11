const React = require('React');
const { Link } = require('react-router');
const activeComponent = require('react-router-active-component');
const NavItem = activeComponent('li');

module.exports = React.createClass({
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
              <li className="dropdown">
                <a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                  Current Identity <span className="caret"></span>
                </a>
                <ul className="dropdown-menu">
                  <li><a href="#">Some other identity</a></li>
                  <li><a href="#">Another identity</a></li>
                  <li><a href="#">3rd identity</a></li>
                  <li role="separator" className="divider"></li>
                  <li><a href="#">Create new identity</a></li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    );
  }
});
