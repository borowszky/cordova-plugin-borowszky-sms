var exec = require('cordova/exec');

var PLUGIN_NAME = 'AcsSmsPlugin';

var AcsSmsPlugin = {};

AcsSmsPlugin.read = function() {
    // fire
    exec(
        success,
        failure,
        'AcsSmsPlugin',
        'read', []
    );
}

AcsSmsPlugin.hasPermission = function(success, failure) {
    // fire
    exec(
        success,
        failure,
        'AcsSmsPlugin',
        'has_permission', []
    );
};

module.exports = AcsSmsPlugin;