const React = require('React');

module.exports = React.createClass({
  propTypes: {
    onUpvote: React.PropTypes.func.isRequired,
    onDownvote: React.PropTypes.func.isRequired,
    torrentList: React.PropTypes.array.isRequired
  },
  generateListItems() {
    return this.props.torrentList.map(({hash, displayName, upvoted, downvoted}) =>
      <li key={hash}>
        {displayName}
        <div className="vote-buttons">
          <a href="#" onClick={() => this.props.onUpvote({hash})}>
            <span className={`glyphicon glyphicon-triangle-top vote-icon upvote ${upvoted ? 'active' : ''}`}>
            </span>
          </a>
          <a href="#" onClick={() => this.props.onDownvote({hash})}>
            <span className={`glyphicon glyphicon-triangle-bottom vote-icon downvote ${downvoted ? 'active' : ''}`}>
            </span>
          </a>
        </div>
      </li>
    );
  },
  render() {
    if (this.props.torrentList.length === 0) {
      return <div>No torrents have been added.</div>;
    }
    return (
      <ul>
        {this.generateListItems()}
      </ul>
    );
  }
});
