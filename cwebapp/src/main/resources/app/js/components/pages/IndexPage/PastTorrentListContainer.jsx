const React = require('React');
const {DispatchMixin, SubscribeToStateChangesMixin} = require('../../mixins');
const PastTorrentList = require('./PastTorrentList.jsx');
const actions = require('../../../redux/actions');

module.exports = React.createClass({
  mixins: [DispatchMixin, SubscribeToStateChangesMixin],
  onUpvote({hash}) {
    this.dispatchAction(actions.upvote({hash}));
  },
  onDownvote({hash}) {
    this.dispatchAction(actions.downvote({hash}));
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
