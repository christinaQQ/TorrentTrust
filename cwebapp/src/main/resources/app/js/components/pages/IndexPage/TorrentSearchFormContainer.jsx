const React = require('React');
const {DispatchMixin, SubscribeToStateChangesMixin} = require('../../mixins');
const TorrentSearchForm = require('./TorrentSearchForm.jsx');

module.exports = React.createClass({
  mixins: [DispatchMixin],
  getTorrentRating({magnetLink}, callback) {
    callback(null, 3.389298);
    console.log('Getting a rating for torrent with magnetLink=' + magnetLink);
  },
  addToTorrentList({magnetLink}) {
    console.log('Adding a torrent with magnetLink=' + magnetLink);
  },
  render() {
    return (
      <TorrentSearchForm
        getTorrentRating={this.getTorrentRating}
        addToTorrentList={this.addToTorrentList}
      />
    );
  }
});
