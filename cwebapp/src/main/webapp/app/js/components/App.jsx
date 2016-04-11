const React = require('React');
const Header = require('./includes/header.jsx');

module.exports = React.createClass({
  render() {
    return (
      <div>
        <Header/>
        <div className="container">
          {this.props.children}
        </div>
      </div>
    );
  }
});
