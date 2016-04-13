const React = require('React');
const Navbar = require('./navbar/Navbar.jsx');

module.exports = React.createClass({
  render() {
    return (
      <div>
        <Navbar/>
        <div className="container">
          {this.props.children}
        </div>
      </div>
    );
  }
});
