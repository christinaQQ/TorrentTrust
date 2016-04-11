const React = require('React');

module.exports = React.createClass({
  render() {
    return (
      <div className="row trust-management">
        <div className="col-xs-12">
          <h2>Add Trusted Key</h2>
          <textarea placeholder="Enter a key to trust the associated identity. Other identities belonging to the same user will not be trusted."></textarea>
          <h2>Trusted Identities</h2>
          <p>John Cena (60b725f1...) <button className="btn delete-button">Delete</button></p>
          <p>Arnold Shwarzenegger (3b5d5c37...) <button className="btn delete-button">Delete</button></p>
          <p>Mickey Mouse (2cd6ee2c...) <button className="btn delete-button">Delete</button></p>
        </div>
      </div>
    );
  }
});
