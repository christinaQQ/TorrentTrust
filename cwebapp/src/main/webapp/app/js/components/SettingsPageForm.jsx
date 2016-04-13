const React = require('React');

module.exports = React.createClass({
  propTypes: {
    currentTrustAlgorithm: React.PropTypes.any.isRequired,
    possibleTrustAlgorithms: React.PropTypes.any.isRequired,
    onFormSubmit: React.PropTypes.func.isRequired
  },
  onSubmit(e) {
    e.preventDefault();
    this.props.onFormSubmit(this.state.activeId, this.state.activeName);
    this.setState({dirty: false});
  },
  getInitialState() {
    const {name, id} = this.props.currentTrustAlgorithm;
    return {dirty: false, activeId: id, activeName: name};
  },
  onRadioClick(e) {
    this.setState({
      dirty: true,
      activeName: e.target.value,
      activeId: e.target.id
    });
  },
  generateLabels() {
    return this.props.possibleTrustAlgorithms.map(({id, name}) =>
      <div key={id}>
        <label>
          <input type="radio"
                 defaultChecked={id === this.props.currentTrustAlgorithm.id}
                 name="algorithm"
                 id={id}
                 value={name}
                 onClick={this.onRadioClick} />
          {name}
        </label>
      </div>
    );
  },
  render() {
    return (
      <form className="settings-form" onSubmit={this.onSubmit}>
        {this.generateLabels()}
        <input
          type="submit"
          className="btn"
          disabled={!this.state.dirty}
          value="Save"/>
      </form>
    );
  }
});
