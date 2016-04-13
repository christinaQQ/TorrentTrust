const React = require('React');

module.exports = React.createClass({
  propTypes: {
    onFormSubmit: React.PropTypes.func.isRequired
  },
  clearInputs() {
    this._input.value = '';
  },
  onSubmit(e) {
    e.preventDefault();
    const {identityName} = this.state;
    if (identityName !== null && identityName.trim() !== '') {
      this.props.onFormSubmit(this.state.identityName);
      this.clearInputs();
      this.setState({dirty: false});
    } else {
      // TODO error msg
    }
  },
  getInitialState() {
    return {dirty: false, identityName: null};
  },
  onInputChange() {
    this.setState({
      dirty: true,
      identityName: this._input.value
    });
  },
  render() {
    return (
      <form className="create-identity-form" onSubmit={this.onSubmit}>
      <div className="form-group">
        <label htmlFor="nameInput">Name</label>
        <input type="text" className="form-control" id="nameInput"
               ref={c => this._input = c}
               onChange={this.onInputChange}
               placeholder="Enter the name of your new identity. A key will be automatically generated."
        />
      </div>
      <input
        type="submit"
        className="btn"
        disabled={!this.state.dirty}
        value="Create"/>
      </form>
    );
  }
});
