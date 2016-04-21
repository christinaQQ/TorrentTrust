'use strict';
var najax = require('najax');
var _ = require('lodash');
var async = require('async');
const magnet = require('magnet-uri');

var magnetLinks = require('./magnetLinks.json');

// request ({
    // url: 'http://localhost:8080/'
// })

async.map(_.range(50), (i, callback) => {
  najax({
    url: 'http://localhost:8080/api/identity',
    type: 'POST',
    data: JSON.stringify({name: `${Math.random()}`}),
    processData: false,
    contentType: 'application/json',
    dataType: 'json'
  })
  .done(data => callback(null, data))
  .fail(e => callback(e));
}, (err, identities) => {
  console.log(identities);
  async.eachSeries(identities, (identity, callback) => {
    console.log('processing identity ' + JSON.stringify(identity));
    const {publicKey, privateKey} = identity;
    najax({
      url: 'http://localhost:8080/api/identity/switch',
      type: 'POST',
      data: JSON.stringify({publicKey}),
      processData: false,
      contentType: 'application/json',
      dataType: 'json'
    }).fail(e => callback(e))
    .done(data => {
      async.map(_.range(20), (i, cb) => {
        var toTrust = identities[Math.floor(Math.random() * identities.length)];
        najax({
          url: 'http://localhost:8080/api/user/trust',
          type: 'POST',
          data: JSON.stringify({publicKey: toTrust.publicKey}),
          processData: false,
          contentType: 'application/json'
        }).fail(e => cb(e))
        .done(data => cb(null, data));
      }, e => {
        if (e) {
          return callback(e);
        }
        async.map(_.range(18), (i, cb) => {
          var magnetLink = magnetLinks[Math.floor(Math.random() * magnetLinks.length)];
          const {xt, dn} = magnet.decode(magnetLink);
          var vote = Math.random() > 0.8 ? 'down' : 'up';
          najax({
            url: `http://localhost:8080/api/object/${xt}/${vote}`,
            type: 'POST',
          }).fail(e => cb(e))
          .done(data => cb(null, data));
        }, callback);
      });
    });
  }, e => console.log(e || 'done seeding'));
});
