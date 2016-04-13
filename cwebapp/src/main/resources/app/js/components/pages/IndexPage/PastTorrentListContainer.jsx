const React = require('React');
const {DispatchMixin, SubscribeToStateChangesMixin} = require('../../mixins');
const PastTorrentList = require('./PastTorrentList.jsx');

module.exports = React.createClass({
  mixins: [DispatchMixin, SubscribeToStateChangesMixin],
  onUpvote({hash}) {
    console.log(`upvoted ${hash}`);
  },
  onDownvote({hash}) {
    console.log(`downvoted ${hash}`);
  },
  render() {
    return (
      <PastTorrentList
        torrentList={this.state.torrent_lists[this.state.current_identity.pubKey]}
        onUpvote={this.onUpvote}
        onDownvote={this.onDownvote}
      />
    );
  }
});
