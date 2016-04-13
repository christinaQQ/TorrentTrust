const React = require('React');
const TorrentSearchFormContainer = require('./TorrentSearchFormContainer.jsx');
const PastTorrentListContainer = require('./PastTorrentListContainer.jsx');

module.exports = React.createClass({
  render() {
    return (
      <div className="index-page">
        <TorrentSearchFormContainer />
        <hr/>
        <PastTorrentListContainer />
      </div>
    );
  }
});
