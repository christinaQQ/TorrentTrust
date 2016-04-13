const React = require('React');
const {DispatchMixin, SubscribeToStateChangesMixin} = require('../../mixins');
const actions = require('../../../redux/actions');
const TorrentSearchForm = require('./TorrentSearchForm.jsx');
const $ = require('jquery');
const magnet = require('magnet-uri');

module.exports = React.createClass({
  mixins: [DispatchMixin, SubscribeToStateChangesMixin],
  getTorrentRating({magnetLink}, callback) {
    const {xt} = magnet.decode(magnetLink || '');
    if (!xt) {
      this.dispatchAction(actions.setErrorMessage('Invalid magnet link.'));
    } else {
      const hash = xt.split(':').pop();
      this.dispatchAction(actions.setLoading(true));
      $.ajax({
        url: `/api/object/${hash}/${this.state.current_trust_algorithm.id}`,
        type: 'GET',
        error(_, e) {
          this.dispatchAction(actions.setErrorMessage(`Error: ${e}`));
        },
        statusCode: {
          200({rating}) {
            callback(rating);
          },
          400() {
            this.dispatchAction(actions.setErrorMessage(`Error: no object exists with hash ${hash}.`));
          }
        }
      })
      .always(() => this.dispatchAction(actions.setLoading(false)));
    }
  },
  addToTorrentList({magnetLink}) {
    this.dispatchAction(actions.addToTorrentList({magnetLink}));
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
