const React = require('React');

module.exports = React.createClass({
  propTypes: {
    addToTorrentList: React.PropTypes.func.isRequired,
    getTorrentRating: React.PropTypes.func.isRequired
  },
  clearInputs() {
    this._textarea.value = '';
  },
  onGetRatingClick() {
    this.props.getTorrentRating({magnetLink: this.state.magnetLink}, rating =>
      this.setState({rating})
    );
  },
  addToTorrentList() {
    this.props.addToTorrentList({magnetLink: this.state.magnetLink});
  },
  getInitialState() {
    return {dirty: false, magnetLink: null, rating: null};
  },
  onTextAreaChange() {
    this.setState({
      dirty: true,
      magnetLink: this._textarea.value,
      rating: undefined
    });
  },
  render() {
    return (
      <form className="torrent-search-form" onSubmit={e => e.preventDefault()}>
      <div className="form-group">
        <label htmlFor="magnetLink">Magnet Link</label>
        <textarea className="form-control" id="magnetLink"
               ref={c => this._textarea = c}
               onChange={this.onTextAreaChange}
               placeholder="Enter a magnet link."
        ></textarea>
      </div>
      <div className="form-group">
        <span className="rating">Rating: {this.state.rating !== undefined ? this.state.rating : 'N/A'}</span>
      </div>
      <a className="btn" href="#" onClick={this.onGetRatingClick} disabled={!this.state.magnetLink}>Get Rating</a>
      <a className="btn" href="#" onClick={this.addToTorrentList} disabled={!this.state.magnetLink}>Add to Torrents</a>
      </form>
    );
  }
});
