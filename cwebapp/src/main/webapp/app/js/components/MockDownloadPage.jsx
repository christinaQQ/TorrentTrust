const React = require('React');

module.exports = React.createClass({
  render() {
    return (
      <div className="download-page">
        <div className="container">
          <div className="row">
            <div className="col-xs-12 text-center">
              <span className="torrenttrust-logo">TorrentTrust</span><br/>
            </div>
          </div>
          <div className="row">
            <div className="col-xs-12 text-center">
              <span className="torrenttrust-subtitle">Decentralized Bittorrent Ranking</span>
            </div>
          </div>
        </div>
        <div className="container">
          <div className="row">
            <div className="col-xs-12 text-center">
              <button className="btn download-button">Download</button>
            </div>
          </div>
        </div>
        <div className="container blurbs">
          <div className="row">
            <div className="col-xs-12 col-md-4 text-center">
              <div className="col-header">Simple.</div>
              <img className="col-img" src="/static/alphabet.svg" style={{width: '95px', height: '90px'}}/>
              <div className="text-justify col-text">
                TorrentTrust is designed to be effortless. Our intuitive UI is completely web-based, so you
                can use your favorite browser on whatever platform you choose. Getting started is easy - just
                download and run the jar file. We even have an iPhone app to make it easy to "trust" your friends!
              </div>
            </div>
            <div className="col-xs-12 col-md-4 text-center">
              <div className="col-header">Secure.</div>
              <img className="col-img" src="/static/lock.svg" style={{width: '90px', height: '90px'}}/>
              <div className="text-justify col-text">
                Our system helps prevent you from downloading not only useless torrents, but malicious ones
                as well. Just pop in a magnet link or a torrent file, and we'll tell you if it looks
                safe to download. Curious about the security properties? We have a <a href="http://google.com">
                whitepaper</a> you might be interested in!
              </div>
            </div>
            <div className="col-xs-12 col-md-4 text-center">
              <div className="col-header">Trust-based.</div>
              <img className="col-img-group" src="/static/group.svg" style={{width: '107px', height: '97px'}}/>
              <div className="text-justify col-text">
                TorrentTrust uses a trust-based approach to help you torrent safely. All the ratings you see come
                from people in <em>your</em> network - not just random strangers with unknown intentions. It also ships
                with several different trust metrics, so you can choose your own level of paranoia.
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
});
