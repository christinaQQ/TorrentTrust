'use strict';
const express = require('express');
const bodyParser = require('body-parser');
const fs = require('fs');
const path = require('path');

const app = express();
const apiRouter = express.Router();
const statePath = path.join(__dirname, './state.json');

// TODO state hydration & stuufff
let currentState = () => JSON.parse(fs.readFileSync(statePath));
const generateBody = require('./generateBody');

// Middlewares
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, '../..')));
//////////////

const ERR_PROB = process.env.ERR_PROB ? Number(process.env.ERR_PROB) : 0.5;

apiRouter.get('/object/:hash/:algo', (req, res) => {
  const jsonResponse = {algo: req.params.algo, rating: Math.random()};
  return Math.random() > ERR_PROB ? res.json(jsonResponse) : res.status(400).send('no such object');
});

apiRouter.route('/user/trust')
.post((req, res) => {
  return Math.random() > ERR_PROB ? res.send('completed') : res.status(400).send('no such user');
})
.delete((req, res) => {
  return Math.random() > ERR_PROB ? res.send('completed') : res.status(400).send('no such user');
});

apiRouter.post('/object/:hash/up', (req, res) => {
  return Math.random() > ERR_PROB ? res.status(200).send() : res.status(400).send('no such object');
});

apiRouter.post('/object/:hash/down', (req, res) => {
  return Math.random() > ERR_PROB ? res.status(200).send() : res.status(400).send('no such object');
});

// apiRouter.post('/generate-key-pair', (req, res) => {
//   const keyPair = {pubKey: Math.random(), privateKey: Math.random()};
//   return Math.random() > ERR_PROB ? res.status(200).send(keyPair) : res.status(500).send('hash table error');
// });

apiRouter.route('/identity')
.get((req, res) => {
  res.json(currentState().user_identities);
})
.post((req, res) => {
  const keyPair = {pubKey: '' + Math.random(), privateKey: '' + Math.random()};
  return Math.random() > ERR_PROB ? res.status(200).json(keyPair) : res.status(500).send('big meaty err');
});
apiRouter.post('/setState', (req, res) => {
  const state = req.body;
  console.log(state);
  fs.writeFile(statePath, JSON.stringify(state, null, '  '), 'utf8', (err) => {
    res.status(err ? 500 : 200).send(err ? err.message : 'success');
  });
});
app.use('/api', apiRouter);

app.get('*', (req, res) =>
  res.set('Content-Type', 'text/html').send(generateBody(currentState()))
);

app.listen(3000, () => console.log(`Listening on port 3000.`));
