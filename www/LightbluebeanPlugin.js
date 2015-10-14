/* global cordova, plugin defined by js-module in plugin.xml so no need to define require and module here */

module.exports = {

    greet: function(name, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "LightbluebeanPlugin", "greet", [name]);
    }

};