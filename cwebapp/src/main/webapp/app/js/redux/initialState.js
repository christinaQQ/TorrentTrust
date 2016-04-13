module.exports = {
  trusted_identities: [
    {name: 'John Cena', hash: '60b725f15727cd6de42' },
    {name: 'Arnold Shwarzenegger', hash: '3b5d5c377cd6de42' },
    {name: 'Mickey Mouse', hash: '2cd6ee2cd6de42' }
  ],
  possible_trust_algorithms: [
    {id: 'EIGENTRUST', name: 'Eigentrust'},
    {id: 'ONLY_FRIENDS', name: 'Only Friends'},
    {id: 'FRIEND_OF_FRIEND', name: 'Friends of friends'}
  ],
  current_trust_algorithm: {id: 'ONLY_FRIENDS', name: 'Only Friends'},
  user_identities: [
    {name: 'John Nash', hash: '60b725f15727cd6de42'},
    {name: 'T.S. Elliott', hash: '3b5d5c377cd6de42'},
    {name: 'Sergey Brin', hash: '2cd6ee2cd6de42'}
  ],
  current_identity: {name: 'John Nash', hash: '60b725f15727cd6de42'},
  torrent_list: [
    {hash: 'c12fe1c06bba254a9dc9f519b335aa7c1367a88adn', displayName: 'Wikipedia Example Torrent', upvoted: true, downvoted: false},
    {hash: '8ad1a7e5900a3a8e8e8e1513351adfb2721fe56b', displayName: 'Stuart Russell - Artificial Intelligence A Modern Approach 3rd Edition, 2010.pdf', upvoted: false, downvoted: false},
    {hash: '89d17a0c1c31a87d53a5c63a79f9a9d7e1f66904', displayName: 'Seinfeld Season 4', upvoted: false, downvoted: true}
  ]
};

// module.exports = window.INITIAL_APP_STATE;
