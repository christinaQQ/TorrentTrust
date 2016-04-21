curl http://$1/api/bootstrap --header 'Accept: application/json' | curl -d @- http://$2/api/bootstrap --header "Content-Type: application/json"
