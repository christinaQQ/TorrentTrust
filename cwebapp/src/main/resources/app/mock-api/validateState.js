const validate = require('jsonschema').validate;
module.exports = ((state) => validate(state, {
  type: 'object',
  properties: {
    error_message: {type: [null, 'string']},
    info_message: {type: [null, 'string']},
    loading: {type: 'boolean'},
    trusted: {
      type: 'object',
      patternProperties: {
        '^.+$': {
          type: 'array',
          items: {
            type: 'object',
            properties: {name: {type: 'string'}, pubKey: {type: 'string'}}
          }
        }
      }
    }
  }
}));
