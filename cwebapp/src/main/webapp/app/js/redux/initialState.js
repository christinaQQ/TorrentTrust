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
  current_identity: {name: 'John Nash', hash: '60b725f15727cd6de42'}
};
