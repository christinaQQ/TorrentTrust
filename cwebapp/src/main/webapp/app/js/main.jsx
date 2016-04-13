// require('bootstrap');
const ReactDOM = require('react-dom');
const React = require('react');
const { Router, Route, IndexRoute, browserHistory } = require('react-router');
const App = require('./components/App.jsx');
const IndexPage = require('./components/IndexPage.jsx');
const MockDownloadPage = require('./components/MockDownloadPage.jsx');
const TrustManagement = require('./components/TrustManagement.jsx');
const SettingsPage = require('./components/SettingsPage.jsx');
const NotFoundPage = require('./components/NotFoundPage.jsx');
const CreateIdentityPage = require('./components/pages/CreateIdentityPage');

ReactDOM.render((
  <Router history={browserHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={IndexPage}/>
      <Route path="/mock-download-page" component={MockDownloadPage}/>
      <Route path="/trust-management" component={TrustManagement}/>
      <Route path="/settings" component={SettingsPage}/>
      <Route path="/newIdentity" component={CreateIdentityPage}/>
      <Route path="*" component={NotFoundPage}/>
    </Route>
  </Router>
), document.getElementById('app-container'));

