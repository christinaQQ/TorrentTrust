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
        type: 'GET'
      })
      .then((data, textStatus, jqXHR) => {
        if (jqXHR.status !== 200) {
          return $.Deferred().reject(jqXHR);
        }
        callback(data.rating);
        return jqXHR;
      })
      .fail(jqXHR => {
        const err = jqXHR.responseText || jqXHR.statusText;
        this.dispatchAction(actions.setErrorMessage(`Error: ${err}!`));
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
