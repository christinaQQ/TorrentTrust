curl http://$1/api/bootstrap | curl -d @- http://$2/api/bootstrap --header "Content-Type: application/json"
