'use strict';
var najax = require('najax');
var _ = require('lodash');
var async = require('async');
const magnet = require('magnet-uri');

var magnetLinks = require('./magnetLinks.json');

async.map(_.range(100), (i, callback) => {
  najax({
    url: '/api/identity',
    type: 'POST',
    data: JSON.stringify({name: `${Math.random()}`}),
    processData: false,
    contentType: 'application/json',
    dataType: 'json'
  })
  .done(data => callback(null, data))
  .fail(e => callback(e));
}, identities => {
  async.eachSeries(identities, (identity, callback) => {
    const {publicKey, privateKey} = identity;
    najax({
      url: '/api/identity/switch',
      type: 'POST',
      data: JSON.stringify({publicKey}),
      processData: false,
      contentType: 'application/json',
      dataType: 'json'
    }).fail(e => callback(e))
    .done(data => {
      async.map(_.range(40), (i, cb) => {
        var toTrust = identities[Math.floor(Math.random() * identities.length)];
        najax({
          url: '/api/user/trust',
          type: 'POST',
          data: JSON.stringify({publicKey: toTrust.publicKey}),
          processData: false,
          contentType: 'application/json'
        }).fail(e => cb(e))
        .done(data => cb(null, data));
      }, e => {
        if (e) {
          callback(e);
        }
        async.map(_.range(40), (i, cb) => {
          var magnetLink = magnetLinks[Math.floor(Math.random() * magnetLinks.length)];
          const {xt, dn} = magnet.decode(magnetLink);
          var vote = Math.random() > 0.8 ? 'down' : 'up';
          najax({
            url: `/api/object/${xt}/${vote}`,
            type: 'POST'
          }).fail(e => cb(e))
          .done(data => cb(null, data));
        }, callback);
      });
    });
  }, e => console.log(e));
});
