module.exports = (state) => `
<html>
  <head>
    <title>TorrentTrust</title>
    <script src="https://use.typekit.net/eqe7tlh.js"></script>
    <script>try{Typekit.load({ async: true });}catch(e){}</script>
    <link rel="stylesheet" href="/app/build/css/main.css"></link>
  </head>
  <body>

    <div id=\"app-container\"></div>
    <script>document.write('<script src="http://' + (location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1"></' + 'script>')</script>
    <script>window.INITIAL_APP_STATE = ${JSON.stringify(state)};</script>
    <script src="/app/build/js/libs.js"></script>
    <script src="/app/build/js/main.js"></script>
  </body>
</html>
`
