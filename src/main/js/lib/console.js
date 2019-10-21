'use strict';

function argsToArray(args) {
  var result = [];
  for (var i = 0; i < args.length; i++) {
    result.push(args[i]);
  }
  return result;
}
function consoleMsg(params) {
  var args = argsToArray(params);
  if (args.length > 1) {
    return java.lang.String.format(args[0], args.slice(1));
  } else {
    return args[0];
  }
}

module.exports = function(logger) {
    return {
        log: function() {
            logger.info(consoleMsg(arguments));
        },
        info: function() {
            logger.info(consoleMsg(arguments));
        },
        warn: function() {
            logger.warn(consoleMsg(arguments));
        },
        error: function() {
            logger.err(consoleMsg(arguments));
        },
        debug: function() {
            logger.info(consoleMsg(arguments));
        }
    }
}
