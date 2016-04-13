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
            <span className={`glyphicon glyphicon-triangle-top ${upvoted ? 'active' : ''}`}>
            </span>
          </a>
          <a href="#" onClick={() => this.props.onDownvote({hash})}>
            <span className={`glyphicon glyphicon-triangle-bottom ${downvoted ? 'active' : ''}`}>
            </span>
          </a>
        </div>
      </li>
    );
  },
  render() {
    return (
      <ul>
        {this.generateListItems()}
      </ul>
    );
  }
});
